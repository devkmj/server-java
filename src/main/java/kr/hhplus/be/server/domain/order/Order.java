package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon userCoupon;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private int totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {}

    public static Order create(User user, UserCoupon coupon, List<OrderItem> items, int totalPrice) {
        Order order = new Order();
        order.user = user;
        order.userCoupon = coupon;
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

    public UserCoupon getUserCoupon() {
        return userCoupon;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
