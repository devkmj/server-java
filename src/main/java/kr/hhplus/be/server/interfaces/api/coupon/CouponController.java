package kr.hhplus.be.server.interfaces.api.coupon;

import kr.hhplus.be.server.application.coupon.service.CouponFacadeService;
import kr.hhplus.be.server.application.coupon.service.CouponRedisInitializer;
import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.coupon.request.CreateCouponRequest;
import kr.hhplus.be.server.interfaces.api.coupon.request.IssueCouponRequest;
import kr.hhplus.be.server.interfaces.api.coupon.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponFacadeService couponFacadeService;
    private final CouponService couponService;
    private final CouponRedisInitializer couponRedisInitializer;

    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody CreateCouponRequest createRequest) {
        Coupon coupon = createRequest.toEntity();
        Coupon saved = couponService.createCoupon(coupon);
        couponRedisInitializer.initCoupon(saved);
        CouponResponse response = CouponResponse.from(saved);
        return ResponseEntity.ok(ApiResponse.success("쿠폰 생성 성공", response));
    }

    @PostMapping("/issue")
    public ResponseEntity issueCoupon(@RequestBody IssueCouponRequest issueCouponRequest) {
        IssueCouponCommand command = IssueCouponCommand.from(issueCouponRequest);
        // Redis 발급 로직 후, 발급 티켓을 즉시 반환
        String ticket = couponFacadeService.issueCoupon(command);
        // 응답으로 쿠폰 ID, 사용자 ID, 발급 티켓만 전달
        Map<String, Object> result = Map.of(
                "couponId", command.getCouponId(),
                "userId", command.getUserId(),
                "ticket", ticket
        );
        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급 요청이 접수되었습니다.", result));
    }

}
