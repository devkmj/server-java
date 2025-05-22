package kr.hhplus.be.server.domain.coupon.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface CouponRepository {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coupon> findById(Long id);
    Coupon save(Coupon coupon);

    void deleteAll();
}
