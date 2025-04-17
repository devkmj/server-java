package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.coupon.dto.IssueCouponCommand;
import kr.hhplus.be.server.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/issue")
    public ResponseEntity issueCoupon(@RequestBody IssueCouponCommand command) {
        couponService.issueCoupon(command);
        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급 성공", null));
    }
}
