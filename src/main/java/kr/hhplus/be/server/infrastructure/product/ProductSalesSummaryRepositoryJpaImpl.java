package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.model.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import org.springframework.stereotype.Repository;

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
    public void incrementQty(Long productId, Long qty) {
        productSalesSummaryJpaRepository.incrementQty(productId, qty);
    }
}
