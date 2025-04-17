package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.model.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public UserCouponService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }


    public UserCoupon findById(Long userCouponId) {
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));
    }

    public void save(UserCoupon userCoupon) {
    }

    public void useUserCoupons(List<UserCoupon> coupons) {
        coupons.forEach(coupon -> {
            Optional<UserCoupon> userCouponOptional = userCouponRepository.findById(coupon.getId());
            if (userCouponOptional.isPresent()) {
                UserCoupon userCoupon = userCouponOptional.get();
                userCoupon.use();
            }
        });
    }
}
