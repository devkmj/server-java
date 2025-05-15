package kr.hhplus.be.server.infrastructure.product.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ProductStockJpaRepository extends JpaRepository<ProductStock, Long> {
    Optional<ProductStock> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM ProductStock ps WHERE ps.product.id = :productId")
    Optional<ProductStock> findByProductIdForUpdate(Long productId);
}
