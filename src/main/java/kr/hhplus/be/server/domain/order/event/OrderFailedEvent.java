package kr.hhplus.be.server.domain.order.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderFailedEvent {
    private Long orderId;

    public OrderFailedEvent(Long orderId) {
        this.orderId = orderId;
    }
}
