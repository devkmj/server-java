package kr.hhplus.be.server.domain.user.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;
import kr.hhplus.be.server.domain.coupon.Coupon;

@Entity
@Table(name = "user_coupon", indexes = {
        @Index(name = "idx_user_coupon_user_id", columnList = "userId"),
        @Index(name = "idx_user_coupon_coupon_id", columnList = "coupon_id"),
        @Index(name = "idx_user_coupon_used", columnList = "used")
})
public class UserCoupon extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    private boolean used;

    protected UserCoupon() {}

    public UserCoupon(Long userId, Coupon coupon) {
        if(userId == null || coupon == null) {
            throw new IllegalArgumentException("유저 ID와 쿠폰은 필수입니다.");
        }
        this.userId = userId;
        this.coupon = coupon;
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
            throw new IllegalArgumentException("유효하지 않은 쿠폰입니디");
        }
    }

    public void markAsUsed() {
        this.used = true;
    }
}
