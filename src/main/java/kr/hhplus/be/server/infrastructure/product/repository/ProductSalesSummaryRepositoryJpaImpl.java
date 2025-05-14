package kr.hhplus.be.server.infrastructure.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class ProductSalesSummaryRepositoryJpaImpl implements ProductSalesSummaryRepository {

    private final ProductSalesSummaryJpaRepository productSalesSummaryJpaRepository;

    public ProductSalesSummaryRepositoryJpaImpl(ProductSalesSummaryJpaRepository productSalesSummaryJpaRepository) {
        this.productSalesSummaryJpaRepository = productSalesSummaryJpaRepository;
    }

    @Override
    public Optional<ProductSalesSummary> findByProductId(Long productId) {
        return productSalesSummaryJpaRepository.findByProductId(productId);
    }

    @Override
    public void save(ProductSalesSummary summary) {
        productSalesSummaryJpaRepository.save(summary);
    }

    @Override
    public void incrementQty(Long productId, Long qty, LocalDate orderedAt) {
        productSalesSummaryJpaRepository.incrementQty(productId, qty, orderedAt);
    }

    @Override
    public Optional<ProductSalesSummary> findByProductIdAndOrderedAt(Long id, LocalDate orderedAt) {
        return productSalesSummaryJpaRepository.findByProductIdAndOrderedAt(id, orderedAt);
    }
}
