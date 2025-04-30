package kr.hhplus.be.server.application.order.listener;

import kr.hhplus.be.server.application.order.CompensationService;
import kr.hhplus.be.server.application.order.InventoryService;
import kr.hhplus.be.server.domain.order.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final InventoryService inventoryService;
    private final CompensationService compensationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentCompleted(PaymentCompletedEvent evt) {
        Long orderId = evt.getOrderId();
        try {
            // 재고 차감 및 주문 상태 확정
            inventoryService.decreaseStockForOrder(orderId);
            inventoryService.confirmOrder(orderId);
        } catch (Exception ex) {
            // 실패 시 보상 로직 실행
            compensationService.handleFailedInventory(orderId, ex.getMessage());
        }
    }
}
