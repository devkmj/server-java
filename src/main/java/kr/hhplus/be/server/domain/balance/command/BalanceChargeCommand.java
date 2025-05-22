package kr.hhplus.be.server.domain.balance.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BalanceChargeCommand {

    @NotNull(message = "유효한 사용자 ID가 필요합니다")
    @Min(value = 1, message = "유효한 사용자 ID가 필요합니다")
    private final Long userId;

    @Min(value = 1, message = "충전 금액은 0보다 커야 합니다")
    private final int amount;

    public BalanceChargeCommand(Long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }
}