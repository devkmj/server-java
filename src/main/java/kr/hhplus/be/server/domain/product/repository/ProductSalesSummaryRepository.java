package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.model.ProductSalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProductSalesSummaryRepository {
    Optional<ProductSalesSummary> findByProductId(Long productId);

    void save(ProductSalesSummary summary);

    void incrementQty(Long productId, Long qty);
}
