package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompensationService {

    private final OrderService orderService;
    private final UserCouponService couponService;
    private final BalanceService balanceService;

    /**
     * 재고 차감 실패 시 쿠폰·잔액을 환불하고 주문을 FAILED 상태로 전이
     */
    @Transactional
    public void handleFailedInventory(Long orderId, String reason) {
        Order order = orderService.getOrder(orderId);
        couponService.rollbackUserCoupons(order.getUserCoupons());
        balanceService.refund(order.getUser(), order.getTotalPrice());
        order.markAsFailed(reason);
        orderService.save(order);
    }
}