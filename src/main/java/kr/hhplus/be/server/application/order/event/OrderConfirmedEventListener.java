package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderConfirmedEventListener {

    @TransactionalEventListener
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        Long orderId = event.getOrderId();
        // 주문 정보 전송
    }

}
