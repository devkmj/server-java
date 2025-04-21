package kr.hhplus.be.server.domain.coupon.command;

import lombok.Getter;

@Getter
public class IssueCouponCommand {

    private Long userId;
    private Long couponId;

    public IssueCouponCommand(Long userId, Long couponId) {
        if(userId == null || userId < 0) {
            throw new IllegalArgumentException("유효한 사용자 ID가 아닙니다");
        }
        if(couponId == null || couponId < 0) {
            throw new IllegalArgumentException("유효한 쿠폰 ID가 아닙니다");
        }
        this.userId = userId;
        this.couponId = couponId;
    }

}
