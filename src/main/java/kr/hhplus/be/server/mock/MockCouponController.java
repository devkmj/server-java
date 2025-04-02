package kr.hhplus.be.server.mock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Mock Coupon", description = "쿠폰 관련 Mock API")
@RestController
@RequestMapping("/mock/coupon")
public class MockCouponController {

    @Operation(summary = "선착순 쿠폰 발급(Mock)", description = "사용자 ID를 기반으로 선착순 쿠폰을 발급하는 Mock API입니다.")
    @PostMapping("/issue")
    public ResponseEntity<String> issueCoupon(@RequestParam Long userId) {
        // 실제 발급 로직 없이 스텁 메시지 반환
        return ResponseEntity.ok("✅ MOCK 쿠폰 발급 완료: userId = " + userId);
    }


    @Operation(summary = "보유 쿠폰 목록 조회(Mock)", description = "사용자가 보유한 쿠폰 리스트를 조회하는 Mock API입니다.")
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUserCoupons(@RequestParam Long userId) {
        List<Map<String, Object>> coupons = List.of(
                Map.of(
                        "userCouponId", 1,
                        "couponId", 22,
                        "rate", 3,
                        "used", false,
                        "validFrom", LocalDateTime.now().minusDays(1).toString(),
                        "validUntil", LocalDateTime.now().plusDays(5).toString()
                ),
                Map.of(
                        "userCouponId", 2,
                        "couponId", 23,
                        "rate", 30,
                        "used", true,
                        "validFrom", LocalDateTime.now().minusDays(10).toString(),
                        "validUntil", LocalDateTime.now().minusDays(2).toString()
                )
        );

        return ResponseEntity.ok(coupons);
    }
}
