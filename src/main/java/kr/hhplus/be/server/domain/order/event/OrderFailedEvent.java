package kr.hhplus.be.server.domain.order.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderFailedEvent {

    private Long orderId;
    private String reason;

    public OrderFailedEvent(Long orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

}
