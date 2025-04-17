package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.user.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserId(Long userId);
    List<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    List<UserCoupon> findAll();
}
