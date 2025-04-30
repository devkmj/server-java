package kr.hhplus.be.server.application.order.listener;

import kr.hhplus.be.server.domain.order.event.OrderFailedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderFailedEventListener {

    @TransactionalEventListener
    public void handleOrderConfirmed(OrderFailedEvent event) {
        Long orderId = event.getOrderId();
        // 주문 실패 후 처리
    }

}
