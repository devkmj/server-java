package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import kr.hhplus.be.server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CouponFacade {

    private final CouponService couponService;
    private final UserService userService;
    private final UserCouponService userCouponService;

    @Transactional
    public UserCoupon issueCoupon(IssueCouponCommand command){
        User user = userService.findByUserId(command.getUserId());
        Coupon coupon = couponService.findCouponById(command.getCouponId());

        UserCoupon newUserCoupon = couponService.issue(coupon, user);
        userCouponService.save(newUserCoupon);
        return newUserCoupon;
    }
}
