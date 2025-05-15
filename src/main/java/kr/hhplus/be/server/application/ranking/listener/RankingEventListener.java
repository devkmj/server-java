package kr.hhplus.be.server.application.ranking.listener;

import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.application.ranking.port.RankingUpdater;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.domain.order.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.event.PaymentCompletedEvent;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RankingEventListener {

    private final RankingUpdater updater;
    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        updater.updateRealtime(order, RankingEventType.order);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(PaymentCompletedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        updater.updateRealtime(order, RankingEventType.paid);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderConfirmed(OrderConfirmedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        updater.updateDaily(order);
        updater.updateRealtime(order, RankingEventType.confirm);
    }

    @EventListener
    public void onProductViewed(ProductViewedEvent evt) {
        updater.updateRealtime(evt.getProductId() , RankingEventType.view);
    }
}
