package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.UserCoupon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public int applyCoupons(List<UserCoupon> coupons, int totalPrice) {
        for (UserCoupon coupon : coupons) {
            coupon.validateUsable();
            totalPrice = coupon.getCoupon().discount(totalPrice);
        }
        return totalPrice;
    }
}
