package kr.hhplus.be.server.application.balance.dto;

import kr.hhplus.be.server.domain.balance.Balance;

public class BalanceResponse {
    private final Long userId;
    private final int balance;

    public BalanceResponse(Long userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static BalanceResponse from(Balance balance) {
        return new BalanceResponse(balance.getUserId(), balance.getBalance());
    }

    public Long getUserId() {
        return userId;
    }

    public int getBalance() {
        return balance;
    }
}
