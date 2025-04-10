package kr.hhplus.be.server.infrastructure.user;

import kr.hhplus.be.server.domain.user.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {
}
