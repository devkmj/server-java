package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.application.balance.dto.BalanceChargeCommand;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.balance.exception.BalanceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public BalanceResponse getBalance(Long userId) {
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseThrow(() -> new BalanceNotFoundException("존재하지 않는 사용자입니다"));
        return BalanceResponse.from(balance);
    }

    @Transactional
    public void charge(BalanceChargeCommand command) {
        Balance balance = balanceRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new BalanceNotFoundException("잔액 정보가 없습니다"));

        balance.charge(command.getAmount()); // 도메인 메서드 호출
        balanceRepository.save(balance);
    }

    public Balance findByUserId(Long userId) {
        return balanceRepository.findByUserId(userId)
                .orElseThrow(() -> new BalanceNotFoundException("잔액 정보가 없습니다"));
    }

    public void useBalance(Balance balance, int totalPrice) {
        balance.deduct(totalPrice);
    }
}
