package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.product.PopularProductResponse;
import kr.hhplus.be.server.application.product.ProductResponse;
import org.springframework.stereotype.Service;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductQueryRepository productQueryRepository;

    public ProductService(ProductRepository productRepository, ProductQueryRepository productQueryRepository) {
        this.productRepository = productRepository;
        this.productQueryRepository = productQueryRepository;
    }

    // 전체 조회
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    // 단건 조회
    public ProductResponse getById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return ProductResponse.from(product);
    }

    // 도메인 객체가 필요한 경우
    public Product findByProductId(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public List<PopularProductResponse> getTop5PopularProducts() {
        return productQueryRepository.findTop5PopularProducts();
    }

}
