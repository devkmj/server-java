package kr.hhplus.be.server.interfaces.api.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderItemCommand;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@RequiredArgsConstructor
public class OrderRequest {
    @NotNull
    private Long userId;

    @NotEmpty
    private List<@Valid OrderItemRequest> items;
    private List<Long> userCouponIds;

    public OrderCommand toCommand() {
        return new OrderCommand(userId,
                items.stream()
                        .map(i -> new OrderItemCommand(i.getProductId(), i.getQty()))
                        .collect(Collectors.toList()),
                userCouponIds
        );
    }

    public OrderRequest(Long userId, List<OrderItemRequest> orderItemRequests, List<Long> userCouponIds) {
        this.userId = userId;
        this.items = orderItemRequests;
        this.userCouponIds = userCouponIds;
    }
}
