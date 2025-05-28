package kr.hhplus.be.server.domain.order.command;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderItemCommand {

    private Long orderId;
    private Long productId;
    private int qty;
    private int price;

    public OrderItemCommand(Long orderId, Long productId, int qty, int price) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
        this.price = price;
    }

    public OrderItemCommand(Long productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

}
