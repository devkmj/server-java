package kr.hhplus.be.server.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository {
    List<Product> findAll();

    Object findById(Long invalidProductId);
}
