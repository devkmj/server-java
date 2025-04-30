package kr.hhplus.be.server.domain.user.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {

    Optional<UserCoupon> findById(Long id);

    UserCoupon save(UserCoupon userCoupon);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findAll();

    void deleteAll();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserCoupon> findByIdForUpdate(Long aLong);
}