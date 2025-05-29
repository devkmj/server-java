package kr.hhplus.be.server.infrastructure.product.event;

import kr.hhplus.be.server.domain.product.event.InventoryFailedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockDecreasedEvent;
import kr.hhplus.be.server.domain.product.event.ProductStockEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProductStockSpringEventPublisher implements ProductStockEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public ProductStockSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(ProductStockDecreasedEvent productStockDecreasedEvent) {
        applicationEventPublisher.publishEvent(productStockDecreasedEvent);
    }

    @Override
    public void publish(InventoryFailedEvent inventoryFailedEvent) {
        applicationEventPublisher.publishEvent(inventoryFailedEvent);
    }
}
