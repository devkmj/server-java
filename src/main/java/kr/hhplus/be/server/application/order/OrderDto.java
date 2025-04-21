package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.user.model.UserCoupon;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    private Long orderId;
    private Long userId;
    private Long productId;
    private int totalPrice;
    private String orderStatus;
    private LocalDateTime createTime;
    private List<OrderItemDto> items;
    private List<UserCoupon> usedCouponIds;

    public OrderDto() {}

    public OrderDto(Long userId, Long productId, int totalPrice, String orderStatus, LocalDateTime createTime, List<OrderItemDto> items, List<UserCoupon> usedCouponIds) {
        this.userId = userId;
        this.productId = productId;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.createTime = createTime;
        this.items = items;
        this.usedCouponIds = usedCouponIds;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreatedDate(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }

    public List<UserCoupon> getUsedCouponIds() {
        return usedCouponIds;
    }

    public void setUsedCouponIds(List<UserCoupon> usedCouponIds) {
        this.usedCouponIds = usedCouponIds;
    }

}
