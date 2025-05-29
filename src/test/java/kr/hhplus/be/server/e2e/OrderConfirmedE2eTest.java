package kr.hhplus.be.server.e2e;

import kr.hhplus.be.server.domain.dataplatform.event.DataPlatformEventPublisher;
import kr.hhplus.be.server.domain.order.OrderEventListenerTest;
import kr.hhplus.be.server.interfaces.api.order.event.OrderEventListener;
import org.mockito.Mock;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
public class OrderConfirmedE2eTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
                    .asCompatibleSubstituteFor("apache/kafka")
    );

    @Autowired
    KafkaTemplate<String, OrderConfirmedEvent> kafkaTemplate;

    private KafkaConsumer<String, OrderConfirmedEvent> consumer;

    @Mock
    DataPlatformEventPublisher dataPlatformEventPublisher;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.producer.key-serializer", () -> StringSerializer.class.getName());
        registry.add("spring.kafka.producer.value-serializer", () -> JsonSerializer.class.getName());

        // consumer 설정 (리스너가 이 설정을 사용합니다)
        registry.add("spring.kafka.consumer.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "test-group");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.key-deserializer", () -> StringDeserializer.class.getName());
        registry.add("spring.kafka.consumer.value-deserializer", () -> JsonDeserializer.class.getName());
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages",
                     () -> "kr.hhplus.be.server.domain.order.event");
    }

    @BeforeEach
    void setUp() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "kr.hhplus.be.server.domain.order.event");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton("data_platform_order_events"));
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @DisplayName("메시지 전송 시 리스너가 메시지를 수신해야 한다 (Awaitility)")
    void 메시지_전송시_리스너가_메시지를_수신해야_한다_Awaitility() {
        // given
        long orderId = 42L;
        OrderConfirmedEvent evt = new OrderConfirmedEvent(orderId);

        // when
        kafkaTemplate.send("data_platform_order_events", evt);

        // then: Awaitility를 사용해 비동기적으로 폴링 후 메시지 수신 여부 검증
        await().atMost(5, SECONDS).untilAsserted(() -> {
            ConsumerRecords<String, OrderConfirmedEvent> records =
                    consumer.poll(Duration.ofMillis(200));
            Assertions.assertFalse(records.isEmpty(), "메시지를 못 받았어요");
            ConsumerRecord<String, OrderConfirmedEvent> rec = records.iterator().next();
            Assertions.assertEquals(orderId, rec.value().getOrderId());
        });
    }
}