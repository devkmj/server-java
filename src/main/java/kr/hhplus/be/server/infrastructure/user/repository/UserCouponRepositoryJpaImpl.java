package kr.hhplus.be.server.infrastructure.user.repository;

import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserCouponRepositoryJpaImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;

    public UserCouponRepositoryJpaImpl(UserCouponJpaRepository userCouponJpaRepository) {
        this.userCouponJpaRepository = userCouponJpaRepository;
    }

    @Override
    public Optional<UserCoupon> findById(Long id) {
        return userCouponJpaRepository.findById(id);
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        try {
            return userCouponJpaRepository.save(userCoupon);
        } catch (DataIntegrityViolationException e) {
            // 중복 발급 방지: user_id + coupon_id 유니크 제약 위반
            throw new IllegalStateException("이미 발급 받은 쿠폰입니다", e);
        }
    }

    @Override
    public boolean existsByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.existsByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public List<UserCoupon> findAll() {
        return userCouponJpaRepository.findAll();
    }

    @Override
    public void deleteAll() {
        userCouponJpaRepository.deleteAll();
    }
}
