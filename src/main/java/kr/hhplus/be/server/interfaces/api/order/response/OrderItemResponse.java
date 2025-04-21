package kr.hhplus.be.server.interfaces.api.order.response;

import kr.hhplus.be.server.domain.order.entity.OrderItem;

public record OrderItemResponse(
        Long id,
        Long productId,
        int qty,
        int price
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getProductId(), item.getQty(), item.getPrice());
    }
}