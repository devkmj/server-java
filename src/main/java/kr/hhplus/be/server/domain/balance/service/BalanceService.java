package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.domain.balance.command.BalanceChargeCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.interfaces.api.balance.response.BalanceResponse;
import kr.hhplus.be.server.domain.balance.exception.BalanceNotFoundException;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public BalanceResponse charge(BalanceChargeCommand command) {
        Balance balance = balanceRepository.findByUserId(command.getUserId())
                .orElseThrow(() -> new BalanceNotFoundException("존재하지 않는 사용자입니다"));

        balance.charge(command.getAmount()); // 도메인 메서드 호출
        balanceRepository.save(balance);
        return BalanceResponse.from(balance);
    }

    @Transactional(readOnly = true)
    public Balance findByUserId(Long userId) {
        return balanceRepository.findByUserId(userId)
                .orElseThrow(() -> new BalanceNotFoundException("존재하지 않는 사용자입니다"));
    }

    @Transactional
    public void refund(User user, int totalPrice){
        Balance balance = balanceRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BalanceNotFoundException("존재하지 않는 사용자입니다"));
        balance.charge(totalPrice);
        balanceRepository.save(balance);
    }

    @Transactional
    public void applyPayment(
            Order order,
            Balance balance
    ) {
        //잔액 차감 처리 (예외 발생 시 전체 롤백)
        balance.deduct(order.getTotalPrice());
    }
}
