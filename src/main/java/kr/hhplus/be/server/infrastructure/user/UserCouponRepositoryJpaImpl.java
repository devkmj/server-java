package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserCouponRepositoryJpaImpl implements UserCouponRepository {
    @Override
    public Optional<UserCoupon> findById(Long id) {
        return Optional.empty();
    }
}
