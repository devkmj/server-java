package kr.hhplus.be.server.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;

@Tag(name = "Mock Product", description = "상품 관련 Mock API")
@RestController
@RequestMapping("/mock/products")
public class MockProductController {

    @Operation(summary = "인기 상품 조회 (Mock)", description = "최근 3일간 가장 많이 팔린 인기 상품 3개를 Mock 데이터로 반환합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<Map<String, Object>>> getPopularProducts() {
        List<Map<String, Object>> popularProducts = List.of(
                Map.of("id", 1, "name", "삼다수", "price", 1000, "sales", 210),
                Map.of("id", 2, "name", "라비앙", "price", 5000, "sales", 180),
                Map.of("id", 3, "name", "맑은 샘물", "price", 3000, "sales", 160)
        );

        return ResponseEntity.ok(popularProducts);
    }
}
