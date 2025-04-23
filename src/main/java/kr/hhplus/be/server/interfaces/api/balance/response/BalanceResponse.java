package kr.hhplus.be.server.interfaces.api.balance.response;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import lombok.RequiredArgsConstructor;

public record BalanceResponse(Long userId, int balance) {

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