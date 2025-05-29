package kr.hhplus.be.server.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageController {
    private final KafkaTemplate<String, String> kafka;

    @PostMapping("/publish")
    public void publish(@RequestBody String msg) {
        System.out.println("PUBLISH MESSAGE ================== " + msg);

        kafka.send("test-topic", msg)
                .thenAccept(result ->
                        System.out.printf("▶ sent to topic=%s partition=%d offset=%d%n",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset())
                )
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }
}