package kr.hhplus.be.server.infrastructure.balance.repository;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
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
