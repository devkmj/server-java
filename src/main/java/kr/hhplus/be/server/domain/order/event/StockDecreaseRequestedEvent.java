package kr.hhplus.be.server.domain.order.event;

import lombok.Getter;

@Getter
public class StockDecreaseRequestedEvent {
    private final Long orderId;

    public StockDecreaseRequestedEvent(Long orderId) {
        this.orderId = orderId;
    }

}
