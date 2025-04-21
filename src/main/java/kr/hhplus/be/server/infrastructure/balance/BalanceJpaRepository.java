package kr.hhplus.be.server.infrastructure.balance;

import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    Optional<Balance> findByUserId(Long userId);
}