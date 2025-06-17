package kr.hhplus.be.server.interfaces.api.order.event;

import kr.hhplus.be.server.domain.balance.event.BalanceDeductFailedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.event.*;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.event.InventoryFailedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockDecreasedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderEventPublisher orderEventPublisher;
    private final OrderService orderService;

    // 결제 완료
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBalanceDeducted(BalanceDeductedEvent evt) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[onBalanceDeducted] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, evt.getClass().getSimpleName(), evt.getOrderId(), evt);
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsBalanceDeducted();
    }

    // 결제 실패
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBalanceDeductFailed(BalanceDeductFailedEvent evt) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[onBalanceDeductFailed] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, evt.getClass().getSimpleName(), evt.getOrderId(), evt);
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsBalanceFailed();
    }

    // 재고 차감 성공
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductStockDecreased(ProductStockDecreasedEvent evt){
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[onProductStockDecreased] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, evt.getClass().getSimpleName(), evt.getOrderId(), evt);
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsConfirmed();
        orderEventPublisher.publish(new OrderConfirmedEvent(evt.getOrderId()));
    }

    // 재고 차감 실패
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onInventoryFailed(InventoryFailedEvent evt){
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[InventoryFailedEvent] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, evt.getClass().getSimpleName(), evt.getOrderId(), evt);
        Order order = orderService.getOrder(evt.getOrderId());
        order.markAsInventoryFailed();
    }

}
