package kr.hhplus.be.server.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@Tag(name = "Mock Order", description = "주문 / 결제 관련 Mock API")
@RestController
@RequestMapping("/mock/orders")
public class MockOrderController {

    @Operation(summary = "주문/결제 요청(Mock)", description = "사용자 ID와 상품 목록, 쿠폰을 기반으로 주문을 시뮬레이션")
    @PostMapping
    public ResponseEntity<String> mockOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok("MOCK 주문 생성 완료 ! userId =" + request.userId());
    }

    public record OrderRequest(
            Long userId,
            List<OrderItem> items,
            Long userCouponId
    ){}

    public record OrderItem(
            Long productId,
            int quantuty
    ) {}
}
