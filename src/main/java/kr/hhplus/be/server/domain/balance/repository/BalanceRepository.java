package kr.hhplus.be.server.domain.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository {
    Optional<Balance> findByUserId(Long userId);
    Balance save(Balance balance);
    void deleteAll();
}