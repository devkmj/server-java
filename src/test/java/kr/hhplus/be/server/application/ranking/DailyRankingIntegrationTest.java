package kr.hhplus.be.server.application.ranking;

import kr.hhplus.be.server.application.order.CompensationService;
import kr.hhplus.be.server.application.order.InventoryService;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("일간 인기상품 랭킹 통합 테스트")
class DailyRankingIntegrationTest {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String DAILY_KEY_PREFIX = "popular:daily:";

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
    }

    @Test
    @DisplayName("결제 완료 이벤트 후 일간 랭킹이 주문 수량 순으로 쌓여 API에서 조회된다")
    void dailyRankingEndToEnd() throws Exception {
        Order order1 = Order.of(testUser);
        OrderItem item1 = new OrderItem(p1,3, 1); // A 상품 3개
        OrderItem item2 = new OrderItem(p2,2, 1); // B 상품 1개
        order1.addItem(item1);
        order1.addItem(item2);
        orderRepo.save(order1);
        orderService.markAsPaid(order1.getId());

        Order order2 = Order.of(testUser);
        OrderItem item3 = new OrderItem(p1,2, 1); // B 상품 1개
        order2.addItem(item3);  // A 상품 2개
        orderRepo.save(order2);
        orderService.markAsPaid(order2.getId());

        String dailyKey = DAILY_KEY_PREFIX + LocalDate.now().format(DATE_FMT);

        Double scoreA = redisTemplate.opsForZSet().score(dailyKey, p1.getId().toString());
        Double scoreB = redisTemplate.opsForZSet().score(dailyKey, p2.getId().toString());

        assertEquals(5.0, scoreA, "A 상품 점수는 주문 수량 합(5)이어야 합니다.");
        assertEquals(2.0, scoreB, "B 상품 점수는 주문 수량 합(1)이어야 합니다.");

        // Controller를 통해 Top2 순서 검증
        mockMvc.perform(get("/products/popular/daily")
                        .param("limit", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].productId", is(p1.getId().intValue())))
                .andExpect(jsonPath("$.data[0].score", is(5.0)))
                .andExpect(jsonPath("$.data[1].productId", is(p2.getId().intValue())))
                .andExpect(jsonPath("$.data[1].score", is(2.0)));
    }
}