package kr.hhplus.be.server.application.balance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.balance.dto.BalanceChargeCommand;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BalanceIntegrationTest {

    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        balanceRepository.deleteAll();
    }

    @Nested
    @DisplayName("잔액 조회")
    class GetBalance {

        @Test
        @DisplayName("잔액이 정상적으로 조회된다")
        void 잔액_조회_성공() throws Exception {
            // given
            Long userId = 1L;
            int amount = 10000;
            balanceRepository.save(new Balance(userId, amount));

            // when & then
            mockMvc.perform(get("/balances/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.balance").value(amount));
        }

        @Test
        @DisplayName("유효하지 않은 사용자일 경우 404 예외가 발생한다")
        void 유효하지_않은_사용자() throws Exception {
            // given
            Long userId = 999L;

            // when & then
            mockMvc.perform(get("/balances/{userId}", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("잔액 조회 실패"))
                    .andExpect(jsonPath("$.data").value("존재하지 않는 사용자입니다"));
        }

    }

    @Nested
    @DisplayName("잔액 충전")
    class charge{

        @Test
        @DisplayName("잔액이 정상적으로 충전된다")
        void 잔액_충전_성공() throws Exception {
            // given
            Long userId = 2L;
            int amount = 10000;
            balanceRepository.save(new Balance(userId, 0));
            BalanceChargeCommand command = new BalanceChargeCommand(userId, amount);
            // when & then
            mockMvc.perform(post("/balances/charge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.balance").value(amount));
        }

        @Test
        @DisplayName("기존 잔액이 존재할 경우, 충전 금액이 누적된다")
        void 잔액_누적() throws Exception {
            // given
            Long userId = 2L;
            int initAmount = 10000;
            int chargeAmount = 10000;
            balanceRepository.save(new Balance(userId, initAmount));
            BalanceChargeCommand command = new BalanceChargeCommand(userId, chargeAmount);

            // when & then
            mockMvc.perform(post("/balances/charge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.userId").value(userId))
                    .andExpect(jsonPath("$.data.balance").value(initAmount+chargeAmount));
        }

        @Test
        @DisplayName("음수 충전 시 예외가 발생한다")
        void 음수_충전_예외_발생() throws Exception {
            // given
            String invalidRequest = """
                    {
                        "userId": 1,
                        "amount": -1000
                    }
            """;

            // when & then
            mockMvc.perform(post("/balances/charge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("요청 실패"))
                    .andExpect(jsonPath("$.data").value("충전 금액은 0보다 커야 합니다"));
        }

        @Test
        @DisplayName("필수값 누락 시 400 응답이 발생한다")
        void 필수값_누락() throws Exception {
            String request = """
            {
                "amount": 1000
            }
            """;

            mockMvc.perform(post("/balances/charge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(request))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("요청 실패"));
        }

        @Test
        @DisplayName("충전 도중 예외 발생 시 DB에 반영되지 않는다")
        void 충전_실패_시_잔액_변화_없음() throws Exception {
            Long userId = 1L;
            balanceRepository.save(new Balance(userId, 5000));

            String invalidRequest = """
                {
                    "userId": 1,
                    "amount": -1000
                }
            """;

            mockMvc.perform(post("/balances/charge")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidRequest))
                    .andExpect(status().isBadRequest());

            // then
            Balance balance = balanceRepository.findByUserId(userId).orElseThrow();
            assertThat(balance.getBalance()).isEqualTo(5000); // 원래 값 유지
        }

    }


}
