package kr.hhplus.be.server.domain.product.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductStockRepository {
    Optional<ProductStock> findByProductId(Long productId);
    ProductStock save(ProductStock productStock);
    Optional<ProductStock> findByProductIdForUpdate(@Param("productId") Long productId);
}