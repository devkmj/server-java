package kr.hhplus.be.server.infrastructure.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

//    @Override
//    public List<PopularProductResponse> findTop5PopularProducts() {
//        return entityManager.createQuery("""
//                SELECT new kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse(
//                    p.id, p.name, p.price, SUM(oi.qty)
//                )
//                FROM OrderItem oi
//                JOIN oi.product p
//                JOIN oi.order o
//                WHERE o.createdAt >= :fromDate
//                GROUP BY p.id, p.name, p.price
//                ORDER BY SUM(oi.qty) DESC
//                """, PopularProductResponse.class)
//                .setParameter("fromDate", LocalDateTime.now().minusDays(3))
//                .setMaxResults(5)
//                .getResultList();
//    }

    @Override
    public List<PopularProductResponse> findTop5PopularProducts() {
        return entityManager.createQuery("""
            SELECT new kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse(
                p.id, p.name, p.price, s.totalQty
            )
            FROM ProductSalesSummary s
            JOIN Product p ON p.id = s.productId
            ORDER BY s.totalQty DESC
            """, PopularProductResponse.class)
                .setMaxResults(5)
                .getResultList();
    }
}
