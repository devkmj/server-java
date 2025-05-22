package kr.hhplus.be.server.domain.product.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class ProductStockDecreasedEvent {
    private List<Long> productIds;
    private Long orderId;

    public ProductStockDecreasedEvent(Long orderId, List<Long> productIds) {
        this.orderId = orderId;
        this.productIds = productIds;
    }
}
