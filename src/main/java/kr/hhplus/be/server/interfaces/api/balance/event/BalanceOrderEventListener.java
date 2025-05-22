package kr.hhplus.be.server.interfaces.api.balance.event;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductFailedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceEventPublisher;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BalanceOrderEventListener {

    private final OrderService orderService;
    private final BalanceService balanceService;
    private final BalanceEventPublisher balanceEventPublisher;

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        try{
            Balance balance = balanceService.findByUserId(order.getUser().getId());
            balanceService.applyPayment(order, balance);
            balanceEventPublisher.publish(new BalanceDeductedEvent(evt.getOrderId(), evt.getProductIds()));
        }catch (Exception e) {
            balanceEventPublisher.publish(new BalanceDeductFailedEvent(evt.getOrderId(), evt.getProductIds()));
        }
    }
}
