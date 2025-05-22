package kr.hhplus.be.server.domain.order.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PaymentCompletedEvent {

    private Long orderId;
    private List<Long> productIds;

    public PaymentCompletedEvent(Long orderId, List<Long> productIds) {
        this.orderId = orderId;
        this.productIds = productIds;
    }
}
