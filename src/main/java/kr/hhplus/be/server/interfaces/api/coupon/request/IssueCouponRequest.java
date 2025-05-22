package kr.hhplus.be.server.interfaces.api.coupon.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class IssueCouponRequest {
    private Long couponId;
    private Long userId;
}
