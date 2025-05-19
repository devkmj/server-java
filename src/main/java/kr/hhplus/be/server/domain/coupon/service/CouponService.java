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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

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
        return newUserCoupon;
    }

    /**
     * 신규 쿠폰을 생성하고 저장합니다.
     * @param coupon 저장할 Coupon 엔티티
     * @return 저장된 Coupon (ID 포함)
     */
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon[] findAllActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> all = couponRepository.findAll();
        return all.stream()
                .filter(c -> !now.isBefore(c.getValidFrom()) && !now.isAfter(c.getValidUntil()))
                .toArray(Coupon[]::new);
    }
}
