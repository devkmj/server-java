package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon", indexes = {
        @Index(name = "idx_coupon_valid_until", columnList = "validUntil"),
        @Index(name = "idx_coupon_valid_from", columnList = "validFrom"),
        @Index(name = "idx_coupon_rate", columnList = "rate"),
        @Index(name = "idx_coupon_valid_range", columnList = "validFrom, validUntil"),
        @Index(name = "idx_coupon_created_at", columnList = "created_at")
})
public class Coupon extends BaseTimeEntity<Coupon> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rate;           // 할인율
    private int totalCount;     // 전체 발급 가능 수량
    private int issuedCount;    // 현재까지 발급된 수량

    private LocalDateTime validFrom;    // 유효기간 시작일
    private LocalDateTime validUntil;   // 유효기간 종료일

    protected Coupon() {}

    public Coupon(int rate, int totalCount, int issuedCount, LocalDateTime validFrom, LocalDateTime validUntil) {
        if (rate <= 0 || rate > 100) {
            throw new IllegalArgumentException("할인율은 1~100 사이여야 합니다");
        }
        if (totalCount <= 0) {
            throw new IllegalArgumentException("총 발급 수량은 0보다 커야 합니다");
        }
        if (validFrom.isAfter(validUntil)) {
            throw new IllegalArgumentException("유효기간 설정이 잘못되었습니다");
        }
        this.rate = rate;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public Coupon(Long id, int rate, int totalCount, int issuedCount, LocalDateTime validFrom, LocalDateTime validUntil) {
        if (rate <= 0 || rate > 100) {
            throw new IllegalArgumentException("할인율은 1~100 사이여야 합니다");
        }
        if (totalCount <= 0) {
            throw new IllegalArgumentException("총 발급 수량은 0보다 커야 합니다");
        }
        if (validFrom.isAfter(validUntil)) {
            throw new IllegalArgumentException("유효기간 설정이 잘못되었습니다");
        }
        this.id = id;
        this.rate = rate;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int rate;
        private int totalCount;
        private int issuedCount = 0;
        private LocalDateTime validFrom;
        private LocalDateTime validUntil;

        public Builder rate(int rate) {
            this.rate = rate;
            return this;
        }

        public Builder totalCount(int totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        public Builder validFrom(LocalDateTime validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public Builder validUntil(LocalDateTime validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public Coupon build() {
            return new Coupon(rate, totalCount, issuedCount, validFrom, validUntil);
        }
    }

    public UserCoupon issue(User user) {
        if (issuedCount >= totalCount) {
            throw new IllegalStateException("발급 가능 수량을 초과했습니다");
        }
        if(!isValidNow()){
            throw new IllegalStateException("유효하지 않은 쿠폰입니다");
        }
        this.issuedCount += 1;

        return new UserCoupon(user.getId(), this);
    }

    // Getter
    public Long getId() {
        return id;
    }

    public int getRate() {
        return rate;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getIssuedCount() {
        return issuedCount;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public boolean isValidNow() {
        LocalDateTime now = LocalDateTime.now();
        return (now.isEqual(validFrom) || now.isAfter(validFrom)) &&
                (now.isEqual(validUntil) || now.isBefore(validUntil));
    }

    public int discount(int totalPrice) {
        int discounted = totalPrice - (totalPrice * rate / 100);
        return Math.max(discounted, 0);  // 최소 0원 보장
    }
}
