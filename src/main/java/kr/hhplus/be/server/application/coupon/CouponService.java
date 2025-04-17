package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.application.user.UserCouponService;
import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponService(CouponRepository couponRepository, UserRepository userRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public int applyCoupons(List<UserCoupon> coupons, int totalPrice) {
        for (UserCoupon coupon : coupons) {
            coupon.validateUsable();
            totalPrice = coupon.getCoupon().discount(totalPrice);
        }
        return totalPrice;
    }

    public void issueCoupon(IssueCouponCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));
        Coupon coupon = couponRepository.findById(command.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));

        if (userCouponRepository.existsByUserIdAndCouponId(command.getUserId(), command.getCouponId())) {
            throw new IllegalStateException("이미 발급 받은 쿠폰입니다");
        }

        UserCoupon userCoupon = coupon.issue(user);
        userCouponRepository.save(userCoupon);
    }

    public List<UserCoupon> retrieveCoupons(List<Long> userCouponIds) { 
        if (userCouponIds == null) {
            return Collections.emptyList();
        }

        return userCouponIds.stream()
                .map(userCouponRepository::findById) // Optional<UserCoupon>
                .filter(Optional::isPresent)         // 존재하는 것만 필터
                .map(Optional::get)                  // 실제 UserCoupon 객체로 변환
                .collect(Collectors.toList());
    }
}
