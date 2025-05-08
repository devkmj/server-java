package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductSalesSummaryRepository {
    Optional<ProductSalesSummary> findByProductId(Long productId);

    void save(ProductSalesSummary summary);

    Optional<ProductSalesSummary> findByProductIdAndOrderedAt(Long id, LocalDate orderedAt);

    void saveAll(List<ProductSalesSummary> batch);
}
