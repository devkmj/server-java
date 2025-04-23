package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.application.order.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.coupon.request.IssueCouponRequest;
import kr.hhplus.be.server.interfaces.api.user.response.UserCouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponFacade couponFacade;

    @PostMapping("/issue")
    public ResponseEntity issueCoupon(@RequestBody IssueCouponRequest issueCouponRequest) {
        IssueCouponCommand command = IssueCouponCommand.from(issueCouponRequest);
        UserCoupon userCoupon = couponFacade.issueCoupon(command);
        UserCouponResponse response = UserCouponResponse.from(userCoupon);
        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급 성공", null));
    }
}
