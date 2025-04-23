package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public int applyCoupons(List<UserCoupon> coupons, int totalPrice) {
        for (UserCoupon coupon : coupons) {
            coupon.validateUsable();
            totalPrice = coupon.getCoupon().discount(totalPrice);
        }
        return totalPrice;
    }

    public Coupon findCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));
    }

    public UserCoupon issue(Coupon coupon, User user) {
        UserCoupon newUserCoupon = coupon.issue(user);
        userCouponRepository.save(newUserCoupon);
        return newUserCoupon;
    }
}
