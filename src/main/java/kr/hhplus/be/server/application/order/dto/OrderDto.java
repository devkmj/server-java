package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderItem;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderDto {
    private final List<OrderItem> orderItems;
    private final int totalPrice;
    private final int discount;
    private final int subtotal;

    public OrderDto(List<OrderItem> orderItems, int subtotal, int discount, int totalPrice) {
        this.orderItems = orderItems;
        this.subtotal   = subtotal;
        this.discount   = discount;
        this.totalPrice = totalPrice;
    }
}
