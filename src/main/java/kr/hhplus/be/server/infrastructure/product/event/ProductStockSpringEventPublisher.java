package kr.hhplus.be.server.infrastructure.product.event;

import kr.hhplus.be.server.domain.product.event.InventoryFailedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockDecreasedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductStockSpringEventPublisher implements ProductStockEventPublisher {
    private final ProductStockSpringEventPublisher eventPublisher;

    @Override
    public void publish(ProductStockDecreasedEvent productStockDecreasedEvent) {
        eventPublisher.publish(productStockDecreasedEvent);
    }

    @Override
    public void publish(InventoryFailedEvent inventoryFailedEvent) {
        eventPublisher.publish(inventoryFailedEvent);
    }
}
