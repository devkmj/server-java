package kr.hhplus.be.server.interfaces.api.user.response;

import kr.hhplus.be.server.domain.user.entity.UserCoupon;

import java.time.LocalDateTime;

public record UserCouponResponse(
        Long userId,
        int rate,
        LocalDateTime validFrom,
        LocalDateTime validUntil,
        Long couponId
) {

    public static UserCouponResponse from(UserCoupon userCoupon) {
        return new UserCouponResponse(
                userCoupon.getUserId(),
                userCoupon.getCoupon().getRate(),
                userCoupon.getCoupon().getValidFrom(),
                userCoupon.getCoupon().getValidUntil(),
                userCoupon.getCoupon().getId()
        );
    }
}