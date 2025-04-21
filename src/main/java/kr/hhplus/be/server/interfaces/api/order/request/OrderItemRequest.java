package kr.hhplus.be.server.interfaces.api.order.request;

import lombok.Getter;

public record OrderItemRequest(Long productId, int qty, int price) {
    public OrderItemRequest(Long productId, int qty, int price) {
        this.productId = productId;
        this.qty = qty;
        this.price = qty * price;
    }

}
