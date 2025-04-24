package kr.hhplus.be.server.domain.user.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;

import java.util.Objects;

@Entity
@Table(name = "user_coupon", indexes = {
        @Index(name = "idx_user_coupon_user_id", columnList = "userId"),
        @Index(name = "idx_user_coupon_coupon_id", columnList = "couponId"),
        @Index(name = "idx_user_coupon_used", columnList = "used"),
},
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
)
public class UserCoupon extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
    private boolean used;

    protected UserCoupon() {}

    public UserCoupon(Long userId, Coupon coupon) {
        this.userId = Objects.requireNonNull(userId, "userId는 필수입니다.");
        this.coupon = Objects.requireNonNull(coupon, "coupon은 필수입니다.");
        this.used = false;
    }

    public void use() {
        if (this.used) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        this.used = true;
    }

    public boolean isUsed() {
        return used;
    }

    public Long getUserId() {
        return userId;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public Long getId() {
        return id;
    }

    public void validateUsable() {
        if (this.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다");
        }
        if (!this.getCoupon().isValidNow()) {
            throw new IllegalArgumentException("유효하지 않은 쿠폰입니다");
        }
    }

    public void markAsUsed() {
        this.used = true;
    }
}
