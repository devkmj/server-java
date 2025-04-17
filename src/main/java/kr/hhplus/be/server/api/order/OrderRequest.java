package kr.hhplus.be.server.api.order;

import java.util.List;

public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
    private List<Long> userCouponIds;

    public OrderRequest(Long userId, List<OrderItemRequest> items, List<Long> userCouponIds) {
        this.userId = userId;
        this.items = items;
        this.userCouponIds = userCouponIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public List<Long> getUserCouponIds() {
        return userCouponIds;
    }

    public void setUserCouponIds(List<Long> userCouponIds) {
        this.userCouponIds = userCouponIds;
    }
}
