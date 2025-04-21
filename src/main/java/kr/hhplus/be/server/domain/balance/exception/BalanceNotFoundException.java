package kr.hhplus.be.server.domain.balance.exception;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException(String message) {
        super(message);
    }
}