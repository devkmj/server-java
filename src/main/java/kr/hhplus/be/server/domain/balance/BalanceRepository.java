package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BalanceRepository {
    Optional<Balance> findByUserId(Long userId);
    Balance save(Balance balance);
    void deleteAll();
}