package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.application.ranking.port.RankingQuery;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.PopularProductService;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/popular")
@RequiredArgsConstructor
public class PopularProductController {

    private final PopularProductService popularProductService;
    private final ProductService productService;
    private final RankingQuery rankingQuery;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PopularProductResponse>>> getTop5() {
        return ResponseEntity.ok(ApiResponse.success("실시간 인기 상품", popularProductService.getTop5PopularProducts()));
    }

    @GetMapping("/daily")
    public ApiResponse<List<PopularProductResponse>> daily(@RequestParam(defaultValue="5") int limit) {
        return ApiResponse.success("일간 인기 상품",toDto(rankingQuery.getTop(PeriodType.DAILY, limit)));
    }

    @GetMapping("/weekly")
    public ApiResponse<List<PopularProductResponse>> weekly(@RequestParam(defaultValue="5") int limit) {
        return ApiResponse.success("주간 인기 상품", toDto(rankingQuery.getTop(PeriodType.WEEKLY, limit)));
    }

    @GetMapping("/realtime")
    public ApiResponse<List<PopularProductResponse>> realtime(@RequestParam(defaultValue="5") int limit) {
        return ApiResponse.success("실시간 인기 상품", toDto(rankingQuery.getTop(PeriodType.REALTIME, limit)));
    }

    private List<PopularProductResponse> toDto(List<RankingItem> topN) {
        return topN.stream().map(item -> {
            Product product = productService.findByProductId(item.getProductId());
            return PopularProductResponse.from(product, item.getScore());
        }).toList();
    }

}
