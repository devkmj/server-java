package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Slf4j
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
        order.items = items;
        order.totalPrice = totalPrice;
        order.status = OrderStatus.PENDING;

        for (OrderItem item : items) {
            item.assignTo(order);
        }

        return order;
    }

    public void markAsConfirmed() {
        log.info("[ORDER][CONFIRMED] 주문 ID: {}, 사용자 ID: {}", this.id, this.user.getId());
        this.status = OrderStatus.CONFIRMED;
    }

    public void markAsFailed(String reason) {
        log.warn("[ORDER][FAILED] 주문 ID: {}, 사용자 ID: {}, 사유: {}", this.id, this.user.getId(), reason);
        this.status = OrderStatus.FAILED;
    }

    public List<OrderItem> getOrderItems() {
        return items;
    }

    public LocalDateTime getCreateTime() {
        return this.createdAt;
    }

}
