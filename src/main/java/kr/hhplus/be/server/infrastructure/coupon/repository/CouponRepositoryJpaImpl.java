package kr.hhplus.be.server.infrastructure.coupon.repository;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryJpaImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    public CouponRepositoryJpaImpl(CouponJpaRepository couponJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public void deleteAll() {
        couponJpaRepository.deleteAll();
    }

    @Override
    public List<Coupon> findAll() {
        return couponJpaRepository.findAll();
    }


}
