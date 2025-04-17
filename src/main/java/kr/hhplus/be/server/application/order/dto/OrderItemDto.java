package kr.hhplus.be.server.application.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    private Long orderItemId;
    private Long productId;
    private int qty;
    private int price;

    public OrderItemDto(Long orderItemId, Long productId, int qty, int price) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.qty = qty;
        this.price = price;
    }
}
