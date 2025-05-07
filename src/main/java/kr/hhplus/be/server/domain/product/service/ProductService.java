package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import kr.hhplus.be.server.interfaces.api.product.response.ProductResponse;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .collect(Collectors.toList());
    }

    public ProductResponse getById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return ProductResponse.from(product);
    }

    public Product findByProductId(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public List<PopularProductResponse> getTop5PopularProducts() {
        return productQueryRepository.findTop5PopularProducts();
    }
}
