package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.product.ProductSalesSummaryFacadeService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.order.event.PaymentCompletedEvent;
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
    private final CompensationService compensationService;
    private final ProductSalesSummaryFacadeService productSalesSummaryFacadeService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(PaymentCompletedEvent evt) {
        Long orderId = evt.getOrderId();
        Order order = orderService.getOrder(orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            logger.info("이미 처리된 주문입니다: {}", orderId);
            return;
        }
        try {
            inventoryService.decreaseStockWithDistributedLock(evt.getOrderId(), evt.getProductIds());
            orderService.confirmOrder(orderId);
            productSalesSummaryFacadeService.updateProductSalesSummary(orderId);
        } catch (Exception ex) {
            // 실패 시 보상 로직 실행
            compensationService.handleFailedInventory(orderId, ex.getMessage());
        }
    }
}
