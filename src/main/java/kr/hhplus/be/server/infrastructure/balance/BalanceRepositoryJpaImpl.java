package kr.hhplus.be.server.infrastructure.balance;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public class BalanceRepositoryJpaImpl implements BalanceRepository{

    private final BalanceJpaRepository jpaRepository;

    public BalanceRepositoryJpaImpl(BalanceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Balance> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Balance save(Balance balance) {
        return jpaRepository.save(balance);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
