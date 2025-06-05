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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceOrderEventListener {

    private final OrderService orderService;
    private final BalanceService balanceService;
    private final BalanceEventPublisher balanceEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onOrderCreated(OrderCreatedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        try{
            log.info("onOrderCreated: {}", evt.getOrderId(), evt);
            Balance balance = balanceService.findByUserId(order.getUser().getId());
            balanceService.applyPayment(order, balance);
            balanceEventPublisher.publish(new BalanceDeductedEvent(evt.getOrderId(), evt.getProductIds()));
        }catch (Exception e) {
            log.error("잔액 차감 이벤트 실패: {}", evt.getOrderId(), e);
            balanceEventPublisher.publish(new BalanceDeductFailedEvent(evt.getOrderId(), evt.getProductIds()));
        }
    }
}
