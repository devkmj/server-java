package kr.hhplus.be.server.infrastructure.product.repository;
 
import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductSalesSummaryJpaRepository extends JpaRepository<ProductSalesSummary, Long> {

    @Modifying
    @Transactional
    @Query("""
        UPDATE ProductSalesSummary s 
        SET s.totalQty = s.totalQty + :qty 
        WHERE s.productId = :productId AND s.orderedAt = :orderedAt
    """)
    void incrementQty(@Param("productId") Long productId, @Param("qty") Long qty, @Param("orderedAt") LocalDate orderedAt);

    Optional<ProductSalesSummary> findByProductIdAndOrderedAt(Long productId, LocalDate orderedAt);

    @Query("""
        SELECT s.productId, SUM(s.totalQty) as total
        FROM ProductSalesSummary s
        WHERE s.orderedAt >= :from
        GROUP BY s.productId
        ORDER BY total DESC
        """)
    List<Object[]> findTopSellingProductsSince(@Param("from") LocalDate from, Pageable pageable);

    Optional<ProductSalesSummary> findByProductId(Long productId);
}
