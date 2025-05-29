package kr.hhplus.be.server.interfaces.api.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageEventListener {

    @KafkaListener(topics = "test-topic")
    public void listen(String message) {
        System.out.println("▶ consumed: " + message);
    }
}
