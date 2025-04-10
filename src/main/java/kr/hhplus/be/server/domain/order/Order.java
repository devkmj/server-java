package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;

@Entity
@Table(name = "order")
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long UserCouponId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalPrice;

    protected Order() {}

    public Order(Long userId, Long UserCouponId, OrderStatus status, int totalPrice) {
        this.userId = userId;
        this.UserCouponId = UserCouponId;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELD;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getUserCouponId() {
        return UserCouponId;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public int getTotalPrice() {
        return totalPrice;
    }
}
