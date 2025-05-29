package kr.hhplus.be.server.interfaces.api.dataplatform.event;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.infrastructure.dataplatform.RestDataPlatformClient;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DataPlatformKafkaListener {

    private final RestDataPlatformClient restDataPlatformClient;
    @KafkaListener(topics = "data_platform_order_events", groupId = "data_platform")
    public void onOrderConfirmedEvent(OrderConfirmedEvent event) {
        restDataPlatformClient.sendOrderConfirmed(event.getOrderId());
    }

}
