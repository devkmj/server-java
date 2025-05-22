package kr.hhplus.be.server.infrastructure.coupon.event;

import kr.hhplus.be.server.domain.coupon.event.CouponEventPublisher;
import kr.hhplus.be.server.domain.coupon.event.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventSpringPublisher implements CouponEventPublisher {
    private final CouponEventPublisher eventPublisher;

    @Override
    public void publish(CouponIssuedEvent couponIssuedEvent) {
        eventPublisher.publish(couponIssuedEvent);
    }
}
