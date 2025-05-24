package kr.hhplus.be.server.interfaces.api.ranking.event;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.application.ranking.port.RankingUpdatePort;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.order.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RankingOrderEventListener {

    private final RankingUpdatePort updater;
    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        updater.update(order, PeriodType.REALTIME, RankingEventType.order);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(BalanceDeductedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        updater.update(order, PeriodType.REALTIME, RankingEventType.paid);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderConfirmed(OrderConfirmedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        updater.update(order, PeriodType.DAILY, RankingEventType.confirm);
        updater.update(order, PeriodType.REALTIME, RankingEventType.confirm);
    }

}
