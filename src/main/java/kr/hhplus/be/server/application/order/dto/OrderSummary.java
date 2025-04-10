package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.OrderItem;

import java.util.List;

public class OrderSummary {
    private final List<OrderItem> orderItems;
    private final int totalPrice;

    public OrderSummary(List<OrderItem> orderItems, int totalPrice) {
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
