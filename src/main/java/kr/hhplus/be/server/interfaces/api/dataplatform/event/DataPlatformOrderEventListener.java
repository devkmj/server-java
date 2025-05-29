package kr.hhplus.be.server.interfaces.api.dataplatform.event;

import kr.hhplus.be.server.domain.dataplatform.event.DataPlatformEventPublisher;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor; 
import lombok.extern.slf4j.Slf4j; 
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformOrderEventListener {

    private final DataPlatformEventPublisher dataPlatformEventPublisher;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderConfirmedEvent(OrderConfirmedEvent event) {
        log.info("[DataPlatformOrderEventListener] 수신된 이벤트: {}", event);
        dataPlatformEventPublisher.publish(event);
    }
}
