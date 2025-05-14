package kr.hhplus.be.server.interfaces.api.order.response;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long userId,
        int totalPrice,
        String orderStatus,
        List<OrderItemResponse> items,
        List<Long> usedCouponIds
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .toList(),
                order.getUserCoupons().stream()
                        .map(UserCoupon::getId)
                        .toList()
        );
    }
}