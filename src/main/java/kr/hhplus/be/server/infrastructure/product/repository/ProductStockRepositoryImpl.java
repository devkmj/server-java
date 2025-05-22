package kr.hhplus.be.server.infrastructure.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.product.repository.ProductStockRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductStockRepositoryImpl implements ProductStockRepository {

    private final ProductStockJpaRepository jpaRepository;

    public ProductStockRepositoryImpl(ProductStockJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ProductStock> findByProductId(Long productId) {
        return jpaRepository.findByProductId(productId);
    }

    @Override
    public ProductStock save(ProductStock productStock) {
        return jpaRepository.save(productStock);
    }

    @Override
    public Optional<ProductStock> findByProductIdForUpdate(Long productId) {
        return jpaRepository.findByProductIdForUpdate(productId);
    }
}