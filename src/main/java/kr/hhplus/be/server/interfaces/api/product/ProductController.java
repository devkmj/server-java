package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import kr.hhplus.be.server.interfaces.api.product.response.ProductResponse;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ApiResponse.success("전체 상품 조회 성공", products);
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProductDetails(@PathVariable Long productId) {
        ProductResponse product = productService.getById(productId);
        return ApiResponse.success("상품 상세 조회 성공", product);
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularProductResponse>>> getPopularProducts() {
        return ResponseEntity.ok(ApiResponse.success("인기 상품 조회 성공", productService.getTop5PopularProducts()));
    }

}
