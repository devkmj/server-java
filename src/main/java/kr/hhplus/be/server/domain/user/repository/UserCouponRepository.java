package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.model.UserCoupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> findById(Long id);

    UserCoupon save(UserCoupon userCoupon);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findAll();

    void deleteAll();
}