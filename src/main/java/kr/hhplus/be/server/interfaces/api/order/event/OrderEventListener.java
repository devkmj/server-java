package kr.hhplus.be.server.interfaces.api.order.event;

import kr.hhplus.be.server.domain.balance.event.BalanceDeductFailedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.event.*;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.event.InventoryFailedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockDecreasedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderEventPublisher orderEventPublisher;
    private final OrderService orderService;

    // 결제 완료
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBalanceDeducted(BalanceDeductedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsBalanceDeducted();
    }

    // 결제 실패
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBalanceDeductFailed(BalanceDeductFailedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsBalanceFailed();
    }

    // 재고 차감 성공
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductStockDecreased(ProductStockDecreasedEvent evt){
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsConfirmed();
        orderEventPublisher.publish(new OrderConfirmedEvent(evt.getOrderId()));
    }

    // 재고 차감 실패
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInventoryFailed(InventoryFailedEvent evt){
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsInventoryFailed();
    }

}
