package kr.hhplus.be.server.domain.balance;

import java.util.Optional;

public interface BalanceRepository {
    Optional<Balance> findByUserId(Long userId);
    Balance save(Balance balance);
}