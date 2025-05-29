package kr.hhplus.be.server.infrastructure.balance.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductFailedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceEventPublisher;

@Component
public class BalanceSpringEventPublisher implements BalanceEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public BalanceSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(BalanceDeductedEvent balanceDeductedEvent) {
        applicationEventPublisher.publishEvent(balanceDeductedEvent);
    }

    @Override
    public void publish(BalanceDeductFailedEvent balanceDeductFailedEvent) {
        applicationEventPublisher.publishEvent(balanceDeductFailedEvent);
    }
}