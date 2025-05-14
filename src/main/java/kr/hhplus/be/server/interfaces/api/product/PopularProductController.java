package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.domain.product.service.PopularProductService;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products/popular")
public class PopularProductController {

    PopularProductService popularProductService;

    @GetMapping("/realtime")
    public ResponseEntity<ApiResponse<List<PopularProductResponse>>> getTop5() {
        return ResponseEntity.ok(ApiResponse.success("실시간 인기 상품", popularProductService.getTop5PopularProducts()));
    }

}
