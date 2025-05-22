package kr.hhplus.be.server.domain.balance.event;

public interface BalanceEventPublisher {
    void publish(BalanceDeductedEvent balanceDeductedEvent);
    void publish(BalanceDeductFailedEvent balanceDeductFailedEvent);
}
