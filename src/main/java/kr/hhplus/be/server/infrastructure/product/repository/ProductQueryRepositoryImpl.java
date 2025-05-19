package kr.hhplus.be.server.infrastructure.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;

@Repository
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PopularProductResponse> findTop5PopularProducts() {
        LocalDate cutoff = LocalDate.now().minusDays(3);
        return entityManager.createQuery(
                        """
                        
                                SELECT new kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse(
                              p.id, p.name, p.price, SUM(s.totalQty) * 1.0
                        )
                        FROM ProductSalesSummary s
                        JOIN Product p
                          ON p.id = s.productId
                        WHERE s.orderedAt >= :cutoff
                          AND p.status <> :deletedStatus
                        GROUP BY p.id, p.name, p.price
                        ORDER BY SUM(s.totalQty) DESC,
                                 COALESCE(s.updatedAt, s.createdAt) DESC
                        """,
                        PopularProductResponse.class
                )
                .setParameter("cutoff", cutoff)
                .setParameter("deletedStatus", ProductStatus.DELETED)
                .setMaxResults(5)
                .getResultList();

    }
}