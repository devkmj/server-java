package kr.hhplus.be.server.domain.order.command;

import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class OrderCommand {

    private Long userId;
    private List<OrderItemCommand> items;
    private List<Long> userCouponIds; // Optional 가능

    public OrderCommand(Long userId, List<OrderItemCommand> items, List<Long> userCouponIds) {
        if(userId == null || userId < 0) {
            throw new IllegalArgumentException("유효한 사용자 ID가 아닙니다");
        }
        if(items.isEmpty()) {
            throw new IllegalArgumentException("선텍힌 싱픔이 존재하지 않습니다");
        }
        this.userId = userId;
        this.items = items;
        this.userCouponIds = userCouponIds;
    }
    public static OrderCommand from(OrderRequest request) {
        OrderCommand command = new OrderCommand();
        command.userId = request.getUserId();
        command.userCouponIds = request.getUserCouponIds();
        command.items = request.getItems().stream()
                .map(item -> new OrderItemCommand(item.getProductId(), item.getQty()))
                .toList();
        return command;
    }

    public static OrderCommand of(Long userId, List<OrderItemCommand> orderItemCommands, List<Long> userCouponIds) {
        OrderCommand command = new OrderCommand();
        command.userId = userId;
        command.items = orderItemCommands;
        command.userCouponIds = userCouponIds;
        return command;
    }


    public List<Long> getProductIds(){
        return this.items.stream().map(OrderItemCommand::getProductId).collect(Collectors.toList());
    }
}
