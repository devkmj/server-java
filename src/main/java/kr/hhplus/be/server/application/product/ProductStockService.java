package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.application.order.dto.OrderItemCommand;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import org.springframework.stereotype.Service;

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

    public void decreaseProductStocks(List<OrderItemCommand> items) {
        items.forEach(item -> {
            ProductStock stock = this.findByProductId(item.getProductId());
            stock.validateEnough(item.getQty());
            stock.decrease(item.getQty());
            productStockRepository.save(stock);
        });
    }
}
