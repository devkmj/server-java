package kr.hhplus.be.server.infrastructure.product.repository;
 
import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProductSalesSummaryJpaRepository extends JpaRepository<ProductSalesSummary, Long> {
    Optional<ProductSalesSummary> findByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ProductSalesSummary s 
        SET s.totalQty = s.totalQty + :qty, 
            s.lastSoldAt = CURRENT_TIMESTAMP 
        WHERE s.productId = :productId
    """)
    void incrementQty(@Param("productId") Long productId, @Param("qty") Long qty);
}
