package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductSalesSummaryRepository {
    Optional<ProductSalesSummary> findByProductId(Long productId);

    void save(ProductSalesSummary summary);

    void incrementQty(Long productId, Long qty, LocalDate orderedAt);

    Optional<ProductSalesSummary> findByProductIdAndOrderedAt(Long id, LocalDate orderedAt);
}
