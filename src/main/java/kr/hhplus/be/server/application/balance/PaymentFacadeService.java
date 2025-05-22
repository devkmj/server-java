package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceEventPublisher;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.event.OrderFailedEvent;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentFacadeService {

    private final OrderService orderService;
    private final UserCouponService couponService;
    private final BalanceService balanceService;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public void completePayment(Long orderId) {
        Order order = orderService.getOrder(orderId);
        try{
            List<UserCoupon> userCoupons = couponService.retrieveCouponsLock(order.getUserCouponIds());
            Balance balance = balanceService.findByUserId(order.getUser().getId());
            balanceService.applyPayment(order, balance);

            List<Long> productIds = order.getOrderItems().stream()
                    .map(OrderItem::getProductId)
                    .sorted()
                    .toList();
            orderEventPublisher.publish(new OrderCreatedEvent(orderId, productIds));
        } catch (Exception e) {
            orderEventPublisher.publish(new OrderFailedEvent(orderId));
        } finally {
            orderService.save(order);
        }
    }
}
