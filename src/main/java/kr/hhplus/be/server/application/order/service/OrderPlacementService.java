package kr.hhplus.be.server.application.order.service;

import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderPlacementService {

    private final UserService userService;
    private final UserCouponService userCouponService;
    private final OrderService orderService;
    private final OrderCalculationService orderCalculationService;
    private final OrderEventPublisher eventPublisher;

    @Transactional
    public Order placeOrder(OrderCommand cmd){
        User user = userService.findByUserId(cmd.getUserId());
        List<UserCoupon> userCoupons = userCouponService.retrieveCoupons(cmd.getUserCouponIds());
        userCouponService.useUserCoupons(userCoupons);
        OrderDto dto = orderCalculationService.calculateOrderItems(cmd.getItems(), userCoupons);
        Order placeOrder = orderService.createPendingOrder(user, dto.getOrderItems(), userCoupons, dto.getTotalPrice());

        eventPublisher.publish(new OrderCreatedEvent(placeOrder.getId(), cmd.getProductIds()));

        return placeOrder;
    }
}
