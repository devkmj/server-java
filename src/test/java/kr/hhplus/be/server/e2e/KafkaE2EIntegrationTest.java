package kr.hhplus.be.server.e2e;

import kr.hhplus.be.server.infrastructure.dataplatform.RestDataPlatformClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.awaitility.Awaitility;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class KafkaE2EIntegrationTest {

    private static final String MULTI_PART_TOPIC = "test-topic-multi";

    @Autowired
    private RestDataPlatformClient restDataPlatformClient;

    @Autowired
    private TestRestTemplate rest;

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
            .asCompatibleSubstituteFor("apache/kafka")
    );

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.producer.key-serializer", () -> StringSerializer.class.getName());
        registry.add("spring.kafka.producer.value-serializer", () -> StringSerializer.class.getName());

        // consumer 설정 (리스너가 이 설정을 사용합니다)
        registry.add("spring.kafka.consumer.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "test-group");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.key-deserializer", () -> StringDeserializer.class.getName());
        registry.add("spring.kafka.consumer.value-deserializer", () -> StringDeserializer.class.getName());
    }

    @Test
    void API_호출_후_토픽에_메시지_나타나고_리스너가_출력한다() {
        // 1) API 호출 → 메시지 전송
        rest.postForEntity("/api/publish", "hello-world", Void.class);

        String brokers = kafka.getBootstrapServers().replace("PLAINTEXT://", "");

        // 올바른 group.id 를 사용하도록 컨슈머 설정 구성
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "e2e-consumer");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (var consumer = new KafkaConsumer<String, String>(consumerProps)) {
            consumer.subscribe(List.of("test-topic"));

            // 구독 후 메시지 폴링 및 검증
            Awaitility.await()
                    .atMost(5, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        ConsumerRecord<String, String> rec =
                                KafkaTestUtils.getSingleRecord(consumer, "test-topic");
                        Assertions.assertEquals("hello-world", rec.value());
                    });
        }
    }

    @Test
    void 여러_파티션_생성_시_생산_소비_후_파티션별_오프셋_증가() throws Exception {
        // 3개의 파티션을 가진 토픽 생성
        Map<String, Object> adminProps = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        try (AdminClient admin = AdminClient.create(adminProps)) {
            NewTopic topic = new NewTopic(MULTI_PART_TOPIC, 3, (short)1);
            try {
                admin.createTopics(List.of(topic)).all().get();
            } catch (ExecutionException e) {
                if (!(e.getCause() instanceof org.apache.kafka.common.errors.TopicExistsException)) {
                    throw e;
                }
            }
        }
        // 프로듀서 설정
        Map<String, Object> prodProps = new HashMap<>();
        prodProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        prodProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        prodProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        KafkaTemplate<String, String> template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(prodProps));
        // 파티션별 메시지 전송
        template.send(MULTI_PART_TOPIC, 0, null, "p0-1");
        template.send(MULTI_PART_TOPIC, 0, null, "p0-2");
        template.send(MULTI_PART_TOPIC, 1, null, "p1-1");
        template.send(MULTI_PART_TOPIC, 1, null, "p1-2");
        template.send(MULTI_PART_TOPIC, 1, null, "p1-3");
        template.send(MULTI_PART_TOPIC, 2, null, "p2-1");
        template.flush();
        // 컨슈머로 위치 확인
        Properties consProps = new Properties();
        consProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        consProps.put(ConsumerConfig.GROUP_ID_CONFIG, "offset-test-group");
        consProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consProps)) {
            List<TopicPartition> partitions = List.of(
                new TopicPartition(MULTI_PART_TOPIC, 0),
                new TopicPartition(MULTI_PART_TOPIC, 1),
                new TopicPartition(MULTI_PART_TOPIC, 2)
            );
            consumer.assign(partitions);
            consumer.poll(Duration.ofSeconds(1));
            assertThat(consumer.position(partitions.get(0))).isEqualTo(2);
            assertThat(consumer.position(partitions.get(1))).isEqualTo(3);
            assertThat(consumer.position(partitions.get(2))).isEqualTo(1);
        }
    }
}
