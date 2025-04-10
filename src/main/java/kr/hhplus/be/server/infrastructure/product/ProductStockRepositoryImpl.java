package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
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
}