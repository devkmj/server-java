package kr.hhplus.be.server.interfaces.api.order.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@RequiredArgsConstructor
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private List<Long> userCouponIds;

    public OrderRequest(Long userId, List<OrderItemRequest> orderItemRequests, List<Long> userCouponIds) {
        this.userId = userId;
        this.items = orderItemRequests;
        this.userCouponIds = userCouponIds;
    }
}
