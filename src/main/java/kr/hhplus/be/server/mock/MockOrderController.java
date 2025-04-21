package kr.hhplus.be.server.mock;

import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<Long>> mockOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("선착순 쿠폰 발급 완료", request.userId));
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
