package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_user_id", columnList = "user_id"),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_created_at", columnList = "created_at"),
        @Index(name = "idx_order_user_status", columnList = "user_id, status")
})
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id")
    private List<UserCoupon> userCoupons;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {}

    public static Order create(User user, List<UserCoupon> userCoupons, List<OrderItem> items, int totalPrice) {
        if (user == null) throw new IllegalArgumentException("주문자는 필수입니다.");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("주문 항목은 1개 이상이어야 합니다.");
        if (totalPrice <= 0) throw new IllegalArgumentException("총 금액은 0보다 커야 합니다.");
        if (userCoupons != null && !user.ownsCoupon(userCoupons)) throw new IllegalArgumentException("해당 쿠폰은 사용자 소유가 아닙니다.");

        Order order = new Order();
        order.user = user;
        order.userCoupons = userCoupons;
        order.items = items; // ✅ 필드에 할당
        order.totalPrice = totalPrice;
        order.status = OrderStatus.PENDING;

        for (OrderItem item : items) {
            item.setOrder(order); // 양방향 연관관계 연결
        }

        return order;
    }

    // 상태 변경
    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELD;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<UserCoupon> getUserCoupon() {
        return userCoupons;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public int getTotalPrice() {
        return this.totalPrice;
    }

    public List<OrderItem> getOrderItems() {
        return items;
    }

    public LocalDateTime getCreateTime() {
        return this.createdAt;
    }

}
