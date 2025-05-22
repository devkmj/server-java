package kr.hhplus.be.server.infrastructure.balance.event;

import kr.hhplus.be.server.domain.balance.event.BalanceDeductFailedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceSpringEventPublisher implements BalanceEventPublisher {
    private final BalanceEventPublisher eventPublisher;

    @Override
    public void publish(BalanceDeductedEvent balanceDeductedEvent) {
        eventPublisher.publish(balanceDeductedEvent);
    }

    @Override
    public void publish(BalanceDeductFailedEvent balanceDeductFailedEvent) {
        eventPublisher.publish(balanceDeductFailedEvent);
    }
}
