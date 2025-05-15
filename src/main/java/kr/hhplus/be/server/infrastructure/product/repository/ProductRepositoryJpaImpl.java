package kr.hhplus.be.server.infrastructure.product.repository;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class ProductRepositoryJpaImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryJpaImpl(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public void saveAll(List<Product> products) {
        productJpaRepository.saveAll(products);
    }

    @Override
    public void deleteAll() {
        productJpaRepository.deleteAll();
    }
}
