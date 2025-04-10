package kr.hhplus.be.server.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStockRepository {
    Optional<ProductStock> findByProductId(Long productId);
    ProductStock save(ProductStock productStock);
}