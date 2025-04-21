package kr.hhplus.be.server.application.balance;

import kr.hhplus.be.server.domain.balance.command.BalanceChargeCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.balance.exception.BalanceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
@DisplayName("BalanceService 단위 테스트")
public class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    @DisplayName("잔액 조회 - 유효한 사용자 ID")
    void 잔액_조회_성공(){
        // given
        Long userId = 1L;
        Balance balance = new Balance(userId, 10000);
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));

        // when
        int result = balanceService.getBalance(userId).getBalance();

        // then
        assertThat(result).isEqualTo(10000);
    }

    @Test
    @DisplayName("잔액 조회 - 존재하지 않는 사용자 ID")
    void 잔액_조회_실패_예외발생(){
        // given
        Long userId = 99L;
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> balanceService.getBalance(userId))
                .isInstanceOf(BalanceNotFoundException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    @DisplayName("잔액 충전 - 0 이하 금액 예외 발생")
    void 잔액_충전_실패_음수_예외발생(){
        // given
        Long userId = 1L;
        Balance balance = new Balance(userId, 0);
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));
        BalanceChargeCommand command = new BalanceChargeCommand(userId, 0);
        // when & then
        assertThatThrownBy(() -> balanceService.charge(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("충전 금액은 0보다 커야 합니다");
    }

    @Test
    @DisplayName("잔액 충전 - 정상 금액")
    void 잔액_충전_성공() {
        // given
        Long userId = 1L;
        Balance balance = new Balance(userId, 10000);
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));
        given(balanceRepository.save(any())).willReturn(balance);

        // when
        balanceService.charge(new BalanceChargeCommand(userId, 5000));

        // then
        assertThat(balance.getBalance()).isEqualTo(15000);
    }


}
