package kr.hhplus.be.server.infrastructure.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.application.product.PopularProductResponse;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<PopularProductResponse> findTop5PopularProducts() {
        return entityManager.createQuery("""
                SELECT new kr.hhplus.be.server.application.product.PopularProductResponse(
                    p.id, p.name, p.price, SUM(oi.qty)
                )
                FROM OrderItem oi
                JOIN oi.product p
                JOIN oi.order o
                WHERE o.createdAt >= :fromDate
                GROUP BY p.id, p.name, p.price
                ORDER BY SUM(oi.qty) DESC
                """, PopularProductResponse.class)
                .setParameter("fromDate", LocalDateTime.now().minusDays(3))
                .setMaxResults(5)
                .getResultList();
    }
}
