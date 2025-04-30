package kr.hhplus.be.server.domain.order.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentCompletedEvent {

    private Long orderId;

    public PaymentCompletedEvent(Long orderId) {
        this.orderId = orderId;
    }
}
