package kr.hhplus.be.server.application.ranking;

import kr.hhplus.be.server.application.order.CompensationService;
import kr.hhplus.be.server.application.order.InventoryService;
import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.application.ranking.port.RankingQuery;
import kr.hhplus.be.server.application.ranking.port.RankingUpdater;
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
import org.junit.jupiter.api.AfterEach;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("실시간 인기상품 랭킹 통합 테스트")
public class RealtimeRankingIntegrationTest {

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
    @Autowired
    RankingUpdater rankingUpdater;
    @Autowired
    RankingQuery rankingQuery;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoSpyBean
    private CompensationService compensationService;

    private User testUser;
    private Product p1, p2;
    private static final String KEY_PREFIX = "popular:realtime:";

    @BeforeEach
    void setUp() {
        // DB 초기화
        orderRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();

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

    @AfterEach
    void cleanRankingKeys() {
        // 테스트에서 사용하는 모든 실시간 랭킹 키 삭제
        Set<String> keys = redisTemplate.keys("popular:realtime:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private List<String> getRecent10minKeys(String eventType) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        // 0, 10 분 전
        return List.of(0, 10).stream()
                .map(minsAgo -> {
                    LocalDateTime t = now.minusMinutes(minsAgo);
                    int min = t.getMinute() / 10 * 10;
                    LocalDateTime rounded = t.withMinute(min).withSecond(0).withNano(0);
                    return KEY_PREFIX + eventType + ":" + rounded.format(fmt);
                })
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("결제 완료 이벤트 발생 시 실시간 랭킹이 반영된다")
    void testPaymentCompletedEventUpdatesRanking() throws Exception {
        Order order1 = Order.of(testUser);
        OrderItem item1 = new OrderItem(p1,3, 1); // A 상품 3개
        OrderItem item2 = new OrderItem(p2,2, 1); // B 상품 1개
        order1.addItem(item1);
        order1.addItem(item2);
        orderRepo.save(order1);
        orderService.markAsPaid(order1.getId());

        // when: Redis 랭킹 조회 (최근 0분, 10분 내 ZSET 모두 확인)
        int totalScore = 0;
        for (String key : getRecent10minKeys("paid")) {
            Double s = redisTemplate.opsForZSet().score(key, p1.getId().toString());
            if (s != null) totalScore += s;
        }

        // then: 상품1이 랭킹에 반영되어야 함
        assertThat(totalScore).isGreaterThan(0);

        // API도 결과 검증
        mockMvc.perform(get("/products/popular/realtime")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productId").value(p1.getId())); // 가장 높은 상품이 p1인지 체크
    }

    @Test
    @DisplayName("상품 조회 이벤트 발생 시 실시간 랭킹이 반영된다")
    void testProductViewedEventUpdatesRanking() throws Exception {
        // when
        rankingUpdater.updateRealtime(p1.getId(), RankingEventType.view);

        // then: 최근 0, 10분 내 키에서 점수 확인
        int totalScore = 0;
        for (String key : getRecent10minKeys("view")) {
            Double s = redisTemplate.opsForZSet().score(key, p1.getId().toString());
            if (s != null) totalScore += s;
        }
        assertThat(totalScore).isGreaterThan(0);

        mockMvc.perform(get("/products/popular/realtime")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productId").value(p1.getId()));
    }

    @Test
    @DisplayName("상품 조회 이벤트 발생 시 정확한 가중치로 점수가 반영된다")
    void testProductViewedEventUpdatesRankingWithWeight() throws Exception {
        double viewWeight = 0.5; // application.yml 기준

        // when
        rankingUpdater.updateRealtime(p1.getId(), RankingEventType.view);

        // then: ZSET엔 1로 적재
        double actualScore = 0;
        for (String key : getRecent10minKeys("view")) {
            Double s = redisTemplate.opsForZSet().score(key, p1.getId().toString());
            if (s != null) actualScore += s;
        }
        assertThat(actualScore).isEqualTo(1.0);

        // 집계 로직 통해서 조회하면 0.5
        double weightedScore = rankingQuery.getRealtimeTop(1).get(0).getScore();
        assertThat(weightedScore).isEqualTo(viewWeight);
    }

    @Test
    @DisplayName("결제 완료 후 재고 차감까지 성공 시 상품별 실시간 랭킹 score가 반영된다")
    void 결제_완료_재고_차감_성공_시_실시간_랭킹_확인() throws Exception {
        // given: 주문 1 (p1: 3개, p2: 1개)
        Order order1 = Order.of(testUser);
        order1.addItem(new OrderItem(p1, 3, 1));
        order1.addItem(new OrderItem(p2, 1, 1));
        orderRepo.save(order1);

        // 결제 완료 처리 (이벤트 발생 → 재고 차감까지)
        orderService.markAsPaid(order1.getId());

        // then: Redis 랭킹 score 확인 (최근 10분 내)
        double p1Score = 0;
        double p2Score = 0;
        for (String key : getRecent10minKeys("paid")) {
            Double s1 = redisTemplate.opsForZSet().score(key, p1.getId().toString());
            Double s2 = redisTemplate.opsForZSet().score(key, p2.getId().toString());
            if (s1 != null) p1Score += s1;
            if (s2 != null) p2Score += s2;
        }
        // p1: 3개, p2: 1개 주문 반영됐는지 체크
        assertThat(p1Score).isEqualTo(3.0);
        assertThat(p2Score).isEqualTo(1.0);

        // API로도 랭킹 결과 확인 (가장 높은 상품이 p1)
        mockMvc.perform(get("/products/popular/realtime")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].productId").value(p1.getId()))
                .andExpect(jsonPath("$.data[0].score").value(24))
                .andExpect(jsonPath("$.data[1].productId").value(p2.getId()))
                .andExpect(jsonPath("$.data[1].score").value(8));
    }
}
