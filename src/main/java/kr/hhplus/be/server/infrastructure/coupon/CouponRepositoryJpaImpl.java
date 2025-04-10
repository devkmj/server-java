package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.UserCoupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CouponRepositoryJpaImpl implements CouponRepository {
    @Override
    public Optional<Coupon> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void save(UserCoupon userCoupon) {

    }

    @Override
    public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return false;
    }
}
