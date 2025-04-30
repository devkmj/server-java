package kr.hhplus.be.server.infrastructure.user.repository;

import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserId(Long userId);
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    List<UserCoupon> findAll();
    Optional<UserCoupon> findByIdForUpdate(Long userId);
}
