package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> findById(Long id);
    void save(UserCoupon userCoupon);
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}