package kr.hhplus.be.server.application.balance.dto;

public class BalanceChargeCommand {

    private final Long userId;
    private final int amount;

    public BalanceChargeCommand(Long userId, int amount) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

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
