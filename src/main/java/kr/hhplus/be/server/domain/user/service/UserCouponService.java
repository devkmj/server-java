package kr.hhplus.be.server.domain.user.service;

import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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
                .map(userCouponRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserCoupon> retrieveCouponsLock(List<Long> userCouponIds) {
        if (userCouponIds == null || userCouponIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userCouponRepository.findAllByIdIn(userCouponIds);
    }

    public void rollbackUserCoupons(List<UserCoupon> userCoupons) {
        userCoupons.forEach(userCouponRepository::save);
    }
}
