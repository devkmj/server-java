package kr.hhplus.be.server.interfaces.api.product.event;

import kr.hhplus.be.server.application.product.InventoryService;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.event.InventoryFailedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockDecreasedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockEventPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ProductStockEventPublisher productStockEventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(BalanceDeductedEvent evt) {
        Long orderId = evt.getOrderId();
        Order order = orderService.getOrder(orderId);
        if (order.getStatus() != OrderStatus.BALANCE_DEDUCTED) {
            logger.info("이미 처리된 주문입니다: {}", orderId);
            return;
        }
        try {
            inventoryService.decreaseStockWithDistributedLock(evt.getOrderId(), evt.getProductIds());
            productStockEventPublisher.publish(new ProductStockDecreasedEvent(evt.getOrderId(), evt.getProductIds()));
        } catch (Exception ex) {
            // 실패 시 보상 로직 실행
            productStockEventPublisher.publish(new InventoryFailedEvent(evt.getOrderId(), evt.getProductIds()));
        }
    }
}
