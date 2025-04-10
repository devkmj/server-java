package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.UserCoupon;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 주문
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 사용자 쿠폰 (nullable 가능성 있음에 주의)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_coupon_id")
    private UserCoupon userCoupon;

    private int qty;

    private int price; // 스냅샷용 상품 가격

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected OrderItem() {}

    public OrderItem(Order order, UserCoupon userCoupon, int qty, int price) {
        this.order = order;
        this.userCoupon = userCoupon;
        this.qty = qty;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalPrice() {
        return qty * price;
    }

    public void applyCoupon(UserCoupon coupon) {
        this.userCoupon = coupon;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
 
}