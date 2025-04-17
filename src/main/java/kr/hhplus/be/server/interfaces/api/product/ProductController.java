package kr.hhplus.be.server.interfaces.api.product;

import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.common.ApiResponse;
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
    public ApiResponse<List<ProductResponse>> 전체_상품_조회() {
        List<ProductResponse> products = productService.getAllProducts();
        return ApiResponse.success("전체 상품 조회 성공", products);
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> 상품_상세_조회(@PathVariable Long productId) {
        ProductResponse product = productService.getById(productId);
        return ApiResponse.success("상품 상세 조회 성공", product);
    }
}
