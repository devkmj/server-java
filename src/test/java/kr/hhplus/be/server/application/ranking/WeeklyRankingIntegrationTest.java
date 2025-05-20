package kr.hhplus.be.server.application.ranking;


import kr.hhplus.be.server.application.order.service.CompensationService;
import kr.hhplus.be.server.application.order.service.InventoryService;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductStockRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("주간 인기상품 랭킹 통합 테스트")
public class WeeklyRankingIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductRepository productRepo;
    @Autowired
    OrderRepository orderRepo;
    @Autowired
    UserRepository userRepo;
    @Autowired
    ProductStockRepository stockRepo;
    @Autowired
    BalanceRepository balanceRepo;
    @Autowired
    OrderService orderService;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoSpyBean
    private CompensationService compensationService;

    private User testUser;
    private Product p1, p2;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;

    @BeforeEach
    void setUp() {
        // DB 초기화
        orderRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();                       // ← 초기화

        // Redis 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // 테스트용 상품 저장
        p1 = productRepo.save(new Product("product1", 1_000, ProductStatus.AVAILABLE));
        p2 = productRepo.save(new Product("product2", 300_000, ProductStatus.ON_SALE));
        stockRepo.save(new ProductStock(p1, 100));
        stockRepo.save(new ProductStock(p2, 100));

        // 테스트용 사용자 생성
        testUser = new User("testUser");
        userRepo.save(testUser);
        balanceRepo.save(new Balance(testUser.getId(),50_000));

        // Preload past 7 days of daily ranking
        LocalDate today = LocalDate.now();
        ZSetOperations<String, String> ops = redisTemplate.opsForZSet();
        for (int i = 1; i <= 7; i++) {
            String key = "popular:daily:" + today.minusDays(i).format(DATE_FMT);
            // product1: +1 each day
            ops.incrementScore(key, p1.getId().toString(), 1);
            // product2: only last 3 days, +2 each
            if (i > 4) {
                ops.incrementScore(key, p2.getId().toString(), 2);
            }
            redisTemplate.expire(key, Duration.ofDays(8));
        }
    }

    @Test
    void weeklyRankingEndToEnd() throws Exception {
        mockMvc.perform(get("/products/popular/weekly")
                        .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].productId", is(p1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].score", is(7.0)))
                .andExpect(jsonPath("$.data[1].productId", is(p2.getId().intValue())))
                .andExpect(jsonPath("$.data[1].score", is(6.0)));
    }

}
