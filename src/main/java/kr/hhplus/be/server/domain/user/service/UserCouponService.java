package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public UserCouponService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    public UserCoupon findById(Long userCouponId) {
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));
    }

    public void save(UserCoupon userCoupon) {
        userCouponRepository.save(userCoupon);
    }

    public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponRepository.existsByUserIdAndCouponId(userId, couponId);
    }

    public void useUserCoupons(List<UserCoupon> coupons) {
        coupons.forEach(coupon -> {
            Optional<UserCoupon> userCouponOptional = userCouponRepository.findById(coupon.getId());
            if (userCouponOptional.isPresent()) {
                UserCoupon userCoupon = userCouponOptional.get();
                userCoupon.use();
            }
        });
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
