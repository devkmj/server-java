package kr.hhplus.be.server.domain.balance.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Balance> findByUserId(Long userId);
    Balance save(Balance balance);
    void deleteAll();
}