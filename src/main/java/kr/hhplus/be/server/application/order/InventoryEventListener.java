package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import kr.hhplus.be.server.domain.order.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final CompensationService compensationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(PaymentCompletedEvent evt) {
        Long orderId = evt.getOrderId();
        Order order = orderService.getOrder(orderId);
        if (order.getStatus() != OrderStatus.PAID) {
            return;
        }
        try {
            // 재고 차감 및 주문 상태 확정
            inventoryService.decreaseStockWithDistributedLock(orderId);
            orderService.confirmOrder(orderId);
        } catch (Exception ex) {
            // 실패 시 보상 로직 실행
            compensationService.handleFailedInventory(orderId, ex.getMessage());
        }
    }
}
