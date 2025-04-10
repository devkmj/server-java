package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderItemCommand;
import kr.hhplus.be.server.application.product.dto.ProductResponse;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductStock;
import org.springframework.stereotype.Service;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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

}
