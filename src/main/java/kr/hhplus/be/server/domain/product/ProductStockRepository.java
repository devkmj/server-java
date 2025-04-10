package kr.hhplus.be.server.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<Product, Long> {
}
