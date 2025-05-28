package kr.hhplus.be.server.domain.product.event;

public interface ProductStockEventPublisher {
    void publish(ProductStockDecreasedEvent productStockDecreasedEvent);
    void publish(InventoryFailedEvent inventoryFailedEvent);
}
