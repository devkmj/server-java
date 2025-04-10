package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.UserCoupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findById(Long id);
    void save(UserCoupon userCoupon);
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}
