package kr.hhplus.be.server.application.balance;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.interfaces.api.order.request.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.domain.balance.command.BalanceChargeCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductStockRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BalanceIntegrationTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

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
        @DisplayName("잔액 충전 시 금액이 음수이면 예외가 발생한다")
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
        @DisplayName("잔액 충전 시 userId 값이 누락되면 400 응답이 발생한다")
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

    @Test
    @DisplayName("동일 유저가 동시에 여러 결제 요청 시 하나만 성공해야 한다")
    void 동시_결제_요청_테스트() throws InterruptedException {
        // given
        User user = userRepository.save(new User("동시성 유저"));
        Balance balance = balanceRepository.save(new Balance(user.getId(), 10000));
        Product product = productRepository.save(new Product("테스트 상품", 10000, ProductStatus.AVAILABLE));
        productStockRepository.save(new ProductStock(product, 1));

        OrderRequest orderRequest = new OrderRequest(
                user.getId(),
                List.of(new OrderItemRequest(product.getId(), 1, product.getPrice())),
                null
        );

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(post("/orders")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(orderRequest)))
                            .andExpect(result -> {
                                int status = result.getResponse().getStatus();
                                if (status == 200) {
                                    successCount.incrementAndGet();
                                } else {
                                    failCount.incrementAndGet();
                                }
                            });
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());

        // 둘 중 하나만 성공해야 한다.
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}
