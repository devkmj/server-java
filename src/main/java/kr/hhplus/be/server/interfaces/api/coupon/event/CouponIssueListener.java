package kr.hhplus.be.server.interfaces.api.coupon.event;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.event.CouponIssuedEvent;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponIssueListener {

    private final CouponService couponService;
    private final UserCouponService userCouponService;

    @Async
    @EventListener
    public void handle(CouponIssuedEvent evt) {
        String ticket = evt.getTicket();
        Coupon coupon = couponService.findCouponById(evt.getCouponId());
        UserCoupon uc = new UserCoupon(evt.getUserId(), coupon, ticket);
        userCouponService.save(uc);
    }
}
