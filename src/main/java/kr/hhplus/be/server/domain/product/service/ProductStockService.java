package kr.hhplus.be.server.domain.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.product.repository.ProductStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductStockService {

    private final ProductStockRepository productStockRepository; 

    public ProductStockService(ProductStockRepository productStockRepository) {
        this.productStockRepository = productStockRepository;
    }

    public ProductStock findByProductId(Long productId) {
        return productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 재고 정보를 찾을 수 없습니다."));
    }

    @Transactional
    public void decreaseStock(Long productId, int qty) {
        ProductStock productStock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 재고 정보를 찾을 수 없습니다."));
        productStock.decrease(qty);
    }
}
