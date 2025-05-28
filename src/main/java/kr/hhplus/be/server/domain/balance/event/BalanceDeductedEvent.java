package kr.hhplus.be.server.domain.balance.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class BalanceDeductedEvent {

    private Long orderId;
    private List<Long> productIds;

    public BalanceDeductedEvent(Long orderId, List<Long> productIds) {
        this.orderId = orderId;
        this.productIds = productIds;
    }
}
