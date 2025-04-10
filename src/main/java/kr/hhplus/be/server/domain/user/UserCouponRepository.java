package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> findById(Long id);
}