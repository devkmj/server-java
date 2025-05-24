package kr.hhplus.be.server.interfaces.api.dataplatform.event;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.infrastructure.dataplatform.RestDataPlatformClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DataPlatformOrderEventListener {

    private final RestDataPlatformClient restDataPlatformClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderConfirmedEvent(OrderConfirmedEvent event) {
        restDataPlatformClient.sendOrderConfirmed(event.getOrderId());
    }
}
