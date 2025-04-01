package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Nested
    @DisplayName("잔액 충전")
    class Charge{

        @Test
        @DisplayName("음수 금액을 충전 시도 시 예외발생")
        void charge_negativeAmount_shouldThrow() {
            User user = new User("지원", 1000);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThrows(IllegalArgumentException.class, () -> {
                userService.charge(1L, -1000);
            });
        }

        @Test
        @DisplayName("0원 충전 시도 사 예외 발생")
        void charge_zeroAmount_shouldThrow() {
            User user = new User("지민", 1000);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThrows(IllegalArgumentException.class, () -> {
                userService.charge(1L, 0);
            });
        }

        @Test
        @DisplayName("최소 충전금액 이하 충전 시도 시 예외발생")
        void charge_minAmount_shouldThrow() {
            User user = new User("지민", 0);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            assertThrows(IllegalArgumentException.class, () -> {
                userService.charge(1L, 999);
            });
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외 발생")
        void charge_userNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                userService.charge(1L, 100);
            });
        }

    }

}