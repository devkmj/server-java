package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.ProductStock;

import java.util.Optional;

public interface ProductStockRepository {
    Optional<ProductStock> findByProductId(Long productId);
    ProductStock save(ProductStock productStock);
}