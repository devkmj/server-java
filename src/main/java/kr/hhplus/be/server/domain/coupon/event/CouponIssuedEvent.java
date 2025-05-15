package kr.hhplus.be.server.domain.coupon.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CouponIssuedEvent {
    private final Long couponId;
    private final Long userId;
    private final String ticket;
}
