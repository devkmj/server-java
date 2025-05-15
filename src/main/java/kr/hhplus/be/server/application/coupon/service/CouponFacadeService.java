package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.event.CouponIssuedEvent;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CouponFacadeService {

    private final CouponService couponService;
    private final UserService userService;
    private final CouponAllocator couponAllocator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String issueCoupon(IssueCouponCommand command){
        // 사용자 및 쿠폰 조회 · 유효기간 검증
        User user   = userService.findByUserId(command.getUserId());
        Coupon coupon = couponService.findCouponById(command.getCouponId());
        if (LocalDateTime.now().isBefore(coupon.getValidFrom())
         || LocalDateTime.now().isAfter(coupon.getValidUntil())) {
            throw new IllegalStateException("발급 가능 기간이 아닙니다.");
        }

        // Redis 선착순 발급
        String ticket = couponAllocator.allocate(command, coupon);

        eventPublisher.publishEvent(
            new CouponIssuedEvent(command.getCouponId(),
                                   command.getUserId(),
                                   ticket)
        );
        return ticket;
    }
}
