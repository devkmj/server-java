package kr.hhplus.be.server.interfaces.api.order.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private List<Long> userCouponIds;

    public OrderRequest(Long userId, List<OrderItemRequest> items, List<Long> userCouponIds) {
        this.userId = userId;
        this.items = items;
        this.userCouponIds = userCouponIds;
    }

}
