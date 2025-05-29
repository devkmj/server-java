package kr.hhplus.be.server.interfaces.api.balance.event;

import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.event.InventoryFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BalanceCompensationListener {

    private final OrderService orderService;
    private final BalanceService balanceService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderInventoryFailed(InventoryFailedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        balanceService.refund(order.getUser(), order.getTotalPrice());
    }
}
