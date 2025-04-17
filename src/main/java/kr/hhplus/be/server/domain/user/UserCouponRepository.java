package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {
    Optional<UserCoupon> findById(Long id);

    UserCoupon save(UserCoupon userCoupon);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findAll();
}