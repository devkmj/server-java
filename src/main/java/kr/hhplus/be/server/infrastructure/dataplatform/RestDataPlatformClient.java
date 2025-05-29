package kr.hhplus.be.server.infrastructure.dataplatform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class RestDataPlatformClient {

    private final WebClient client;

    public RestDataPlatformClient(WebClient.Builder builder) {
        this.client = builder
                .baseUrl("https://external-data-platform.example.com")
                .build();
    }

    public void sendOrderConfirmed(Long orderId) {
        System.out.println("sendOrderConfirmed " + orderId);
        client.post()
                .uri("/orders/confirmed")
                .bodyValue(Map.of("orderId", orderId))
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
