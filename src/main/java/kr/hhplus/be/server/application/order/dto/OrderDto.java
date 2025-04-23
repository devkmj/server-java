package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.entity.OrderItem;

import java.util.List;

public class OrderDto {
    private final List<OrderItem> orderItems;
    private final int totalPrice;

    public OrderDto(List<OrderItem> orderItems, int totalPrice) {
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
