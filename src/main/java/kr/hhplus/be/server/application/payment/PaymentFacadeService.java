package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacadeService {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UserCouponService couponService;
    private final BalanceService balanceService;

    @Transactional
    public void completePayment(Long orderId) {
        Order order = orderService.getOrder(orderId);
        try{
            List<UserCoupon> userCoupons = couponService.retrieveCouponsLock(order.getUserCouponIds());
            Balance balance = balanceService.findByUserId(order.getUser().getId());
            paymentService.applyPayment(order, userCoupons, balance);
            orderService.save(order);
        } catch (Exception e) {
            order.markAsFailed(e.getMessage());
            orderService.save(order);
            throw e;
        }

    }
}
