package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.product.PopularProductResponse;
import kr.hhplus.be.server.application.product.ProductResponse;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductQueryRepository productQueryRepository;

    private final ProductSalesSummaryRepository productSalesSummaryRepository;

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

    public void updateProductSalesSummary(Product product, int qty) {
        Optional<ProductSalesSummary> existing = productSalesSummaryRepository.findByProductId(product.getId());

        if (existing.isPresent()) {
            productSalesSummaryRepository.incrementQty(product.getId(), (long) qty);
        } else {
            ProductSalesSummary summary = new ProductSalesSummary(
                    product.getId(),
                    (long) qty,
                    LocalDateTime.now()
            );
            productSalesSummaryRepository.save(summary);
        }
    }

}
