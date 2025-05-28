package kr.hhplus.be.server.domain.balance.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BalanceDeductFailedEvent {
    private Long orderId;
    private List<Long> productIds;

    public BalanceDeductFailedEvent(Long orderId, List<Long> productIds) {
        this.orderId = orderId;
        this.productIds = productIds;
    }
}
