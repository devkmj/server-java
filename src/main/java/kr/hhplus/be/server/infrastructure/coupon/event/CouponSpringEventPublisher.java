package kr.hhplus.be.server.infrastructure.coupon.event;

import kr.hhplus.be.server.domain.coupon.event.CouponEventPublisher;
import kr.hhplus.be.server.domain.coupon.event.CouponIssuedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "event.coupon.publisher",
        havingValue = "spring",
        matchIfMissing = true
)
@RequiredArgsConstructor
public class CouponSpringEventPublisher implements CouponEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(CouponIssuedEvent couponIssuedEvent) {
        applicationEventPublisher.publishEvent(couponIssuedEvent);
    }
}
