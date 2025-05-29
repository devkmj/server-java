package kr.hhplus.be.server.interfaces.api.coupon.event;

import kr.hhplus.be.server.domain.balance.event.BalanceDeductFailedEvent;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.event.InventoryFailedEvent;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CouponCompensationListener {

    private final OrderService orderService;
    private final UserCouponService couponService;

    // 주문 잔액 차감 실패 시 보상처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderBalanceFailed(BalanceDeductFailedEvent evt){
        Order order = orderService.getOrder(evt.getOrderId());
        couponService.rollbackUserCoupons(order.getUserCoupons());
    }

    // 주문 재고 차감 실패 시 보상처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderInventoryFailed(InventoryFailedEvent evt) {
        Order order = orderService.getOrder(evt.getOrderId());
        couponService.rollbackUserCoupons(order.getUserCoupons());
    }
}
