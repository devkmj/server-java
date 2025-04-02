package kr.hhplus.be.server.mock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
