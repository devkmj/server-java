package kr.hhplus.be.server.domain.user.event;

import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class RestoreUserCouponEvent {

    List<UserCoupon> userCoupons;

    public RestoreUserCouponEvent(List<UserCoupon> userCoupons) {
        this.userCoupons = userCoupons;
    }
}
