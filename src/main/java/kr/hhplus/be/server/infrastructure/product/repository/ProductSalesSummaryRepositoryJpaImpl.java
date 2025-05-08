package kr.hhplus.be.server.infrastructure.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductSalesSummaryRepositoryJpaImpl implements ProductSalesSummaryRepository {

    private final ProductSalesSummaryJpaRepository productSalesSummaryJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
    public Optional<ProductSalesSummary> findByProductIdAndOrderedAt(Long id, LocalDate orderedAt) {
        return productSalesSummaryJpaRepository.findByProductIdAndOrderedAt(id, orderedAt);
    }

    @Override
    public void saveAll(List<ProductSalesSummary> batch) {
        productSalesSummaryJpaRepository.saveAll(batch);
    }
}
