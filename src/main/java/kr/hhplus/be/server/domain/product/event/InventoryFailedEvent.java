package kr.hhplus.be.server.domain.product.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@RequiredArgsConstructor
public class InventoryFailedEvent {
    private Long orderId;
    private List<Long> productIds;

    public InventoryFailedEvent(Long orderId, List<Long> productIds) {
        this.orderId = orderId;
        this.productIds = productIds;
    }
}
