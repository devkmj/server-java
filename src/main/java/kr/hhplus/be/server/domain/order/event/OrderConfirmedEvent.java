package kr.hhplus.be.server.domain.order.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderConfirmedEvent {

    private Long orderId;

    public OrderConfirmedEvent(Long orderId) {
        this.orderId = orderId;
    }
}
