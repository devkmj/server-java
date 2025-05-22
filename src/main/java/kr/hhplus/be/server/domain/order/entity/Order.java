package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_user_id", columnList = "user_id"),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_created_at", columnList = "created_at"),
        @Index(name = "idx_order_user_status", columnList = "user_id, status")
})
public class Order extends BaseTimeEntity<Order> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id")
    private List<UserCoupon> userCoupons = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {}

    /**
     * 주문 생성 팩토리 메서드 (PENDING)
     */
    public static Order createPending(
            User user,
            List<OrderItem> items,
            List<UserCoupon> coupons,
            int totalPrice
    ) {
        validateInitial(user, items, coupons, totalPrice);

        Order order = new Order();
        order.user = user;
        order.items.addAll(items);
        order.totalPrice = totalPrice;
        order.status = OrderStatus.PENDING;

        // 양방향 연관관계 설정
        items.forEach(item -> item.assignTo(order));

        if (coupons != null && !coupons.isEmpty()) {
            order.userCoupons.addAll(coupons);
        }
        return order;
    }

    private static void validateInitial(
            User user,
            List<OrderItem> items,
            List<UserCoupon> coupons,
            int totalPrice
    ) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("주문자는 필수입니다.");
        }
        if (Objects.isNull(items) || items.isEmpty()) {
            throw new IllegalArgumentException("주문 항목은 최소 1개 이상이어야 합니다.");
        }
        if (totalPrice <= 0) {
            throw new IllegalArgumentException("총 금액은 0보다 커야 합니다.");
        }
        if (Objects.nonNull(coupons) && !user.ownsCoupon(coupons)) {
            throw new IllegalArgumentException("해당 쿠폰은 사용자 소유가 아닙니다.");
        }
    }
    public void markAsBalanceDeducted(){
        ensureStatus(OrderStatus.PENDING);
        this.status = OrderStatus.BALANCE_DEDUCTED;
    }

    public void markAsBalanceFailed(){
        ensureStatus(OrderStatus.PENDING);
        this.status = OrderStatus.BALANCE_FAILED;
    }

    public void markAsInventoryFailed(){
        ensureStatus(OrderStatus.BALANCE_DEDUCTED);
        this.status = OrderStatus.INVENTORY_FAILED;
    }

    public void markAsConfirmed() {
        ensureStatus(OrderStatus.BALANCE_DEDUCTED);
        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * 주문 항목 리스트 반환 (불변)
     */
    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * 적용된 쿠폰 ID 리스트 반환
     */
    public List<Long> getUserCouponIds() {
        return userCoupons.stream()
                .map(UserCoupon::getId)
                .collect(Collectors.toList());
    }

    private void ensureStatus(OrderStatus expected) {
        if (this.status != expected) {
            throw new IllegalStateException("주문 상태가 올바르지 않습니다");
        }
    }

    public void addItem(OrderItem orderItem) {
        this.items.add(orderItem);
        orderItem.assignTo(this);
    }

    /**
     * user와 빈 주문 인스턴스를 반환
     */
    public static Order of(User user) {
        Order order = new Order();
        order.user = user;
        order.status = OrderStatus.PENDING;
        order.totalPrice = 0;
        return order;
    }
}
