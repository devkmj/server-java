package kr.hhplus.be.server.domain.order.command;

import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;

import java.util.List;

public class OrderCommand {

    private Long userId;
    private List<OrderItemCommand> items;
    private List<Long> userCouponIds; // Optional 가능

    public OrderCommand(Long userId, List<OrderItemCommand> items, List<Long> userCouponIds) {
        this.userId = userId;
        this.items = items;
        this.userCouponIds = userCouponIds;
    }

    public OrderCommand() {};

    public static OrderCommand from(OrderRequest request) {
        OrderCommand command = new OrderCommand();
        command.userId = request.getUserId();
        command.userCouponIds = request.getUserCouponIds();
        command.items = request.getItems().stream()
                .map(item -> new OrderItemCommand(item.productId(), item.qty()))
                .toList();
        return command;
    }

    public static OrderCommand of(Long userId, List<OrderItemCommand> items, int qty, List<Long> userCouponIds) {
        return new OrderCommand(userId, items, userCouponIds);
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderItemCommand> getItems() {
        return items;
    }

    public List<Long> getUserCouponIds() {
        return userCouponIds;
    }
}
