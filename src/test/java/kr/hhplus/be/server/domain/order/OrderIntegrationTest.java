package kr.hhplus.be.server.domain.order;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.interfaces.api.order.request.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductStockRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.awaitility.Awaitility.await;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Rollback(false)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Order 통합 테스트")
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductStockRepository productStockRepository;
    @Autowired
    private BalanceRepository balanceRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @BeforeEach
    void setUp() {
        // 데이터 초기화 (User, Product, Stock, Balance)
        User user = userRepository.save(new User("통합테스트유저"));
        Product product = productRepository.save(new Product("상품1", 10000, ProductStatus.AVAILABLE));
        productStockRepository.save(new ProductStock(product, 10));
        balanceRepository.save(new Balance(user.getId(), 50000));
    }

    @Test
    @DisplayName("정상 주문 요청 시 주문에 성공한다")
    void 주문_성공() throws Exception {
        // given
        Long userId = userRepository.findAll().get(0).getId();
        Product product = productRepository.findAll().get(0);
        int qty = 3;
        var orderRequest = new OrderRequest(userId, List.of(new OrderItemRequest(product.getId(), qty, product.getPrice())), null);

        // when & then
        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequest))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.data.items[0].qty").value(qty));

    }

    @Test
    @DisplayName("쿠폰 적용 주문 요청 시 할인된 금액으로 주문에 성공한다")
    void 쿠폰_적용_주문_성공() throws Exception {
        // given
        User user = userRepository.findAll().get(0);
        Product product = productRepository.findAll().get(0);
        int qty = 3;
        int originalTotalPrice = product.getPrice() * qty;

        Coupon coupon = couponRepository.save(new Coupon(10, 100, 2,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(10)));

        UserCoupon userCoupon = new UserCoupon(user.getId(), coupon);
        userCouponRepository.save(userCoupon);

        var orderRequest = new OrderRequest(
                user.getId(),
                List.of(new OrderItemRequest(product.getId(), qty, product.getPrice())),
                List.of(userCoupon.getId())
        );

        // when & then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(user.getId()))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.data.totalPrice").value((int) (originalTotalPrice * 0.9))) // 10% 할인
                .andExpect(jsonPath("$.data.usedCouponIds[0]").value(userCoupon.getId()));
    }

    @Test
    @DisplayName("여러개의 쿠폰 사용 시 누적 할인 금액으로 주문에 성공한다")
    void 다중_쿠폰_사용() throws Exception {
        User user = userRepository.findAll().get(0);
        Product product = productRepository.findAll().get(0);
        int qty = 3;
        int originalTotalPrice = product.getPrice() * qty;

        Coupon coupon1 = couponRepository.save(new Coupon(10, 100, 10, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5)));
        Coupon coupon2 = couponRepository.save(new Coupon(20, 100, 10, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5)));

        UserCoupon userCoupon1 = userCouponRepository.save(new UserCoupon(user.getId(), coupon1));
        UserCoupon userCoupon2 = userCouponRepository.save(new UserCoupon(user.getId(), coupon2));

        var orderRequest = new OrderRequest(
                user.getId(),
                List.of(new OrderItemRequest(product.getId(), qty, product.getPrice())),
                List.of(userCoupon1.getId(), userCoupon2.getId())
        );

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPrice").value((int) (originalTotalPrice * 0.9 * 0.8)));
    }

    @Test
    @DisplayName("이미 사용된 쿠폰으로 주문 시 예외 발생")
    void 사용된_쿠폰_예외() throws Exception {
        User user = userRepository.findAll().get(0);
        Product product = productRepository.findAll().get(0);

        Coupon coupon = couponRepository.save(new Coupon(10, 100, 1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5)));
        UserCoupon userCoupon = new UserCoupon(user.getId(), coupon);
        userCoupon.markAsUsed();
        userCouponRepository.save(userCoupon);

        var orderRequest = new OrderRequest(user.getId(),
                List.of(new OrderItemRequest(product.getId(), 1, product.getPrice())),
                List.of(userCoupon.getId()));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("이미 사용된 쿠폰입니다"));
    }

    @Test
    @DisplayName("만료된 쿠폰 사용하여 주문 시도 시 예외가 발생한다")
    void 만료된_쿠폰_예외() throws Exception {
        User user = userRepository.findAll().get(0);
        Product product = productRepository.findAll().get(0);

        Coupon expiredCoupon = couponRepository.save(new Coupon(10, 100, 1, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1)));
        UserCoupon userCoupon = userCouponRepository.save(new UserCoupon(user.getId(), expiredCoupon));

        var orderRequest = new OrderRequest(user.getId(),
                List.of(new OrderItemRequest(product.getId(), 1, product.getPrice())),
                List.of(userCoupon.getId()));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("유효하지 않은 쿠폰입니다"));
    }

    @Test
    @DisplayName("다른 유저의 쿠폰 사용하여 주문 시 예외 발생한다")
    void 다른_유저_코폰_사용_예외() throws Exception {
        User user1 = userRepository.findAll().get(0);
        User user2 = userRepository.save(new User("다른 유저"));
        Product product = productRepository.findAll().get(0);

        Coupon coupon = couponRepository.save(new Coupon(10, 100, 1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5)));
        UserCoupon userCoupon = userCouponRepository.save(new UserCoupon(user2.getId(), coupon));

        var orderRequest = new OrderRequest(user1.getId(),
                List.of(new OrderItemRequest(product.getId(), 1, product.getPrice())),
                List.of(userCoupon.getId()));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("해당 쿠폰은 사용자 소유가 아닙니다.")); // 예외 메시지 맞춰서 수정
    }

    @Test
    @DisplayName("주문 생성 시 해당 상품의 재고가 부족할 경우 예외가 발생한다")
    void 재고_부족_예외() throws Exception {
        User user = userRepository.findAll().get(0);
        Product product = productRepository.findAll().get(0);

        var orderRequest = new OrderRequest(user.getId(),
                List.of(new OrderItemRequest(product.getId(), 100, product.getPrice())),
                null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("상품 재고가 부족합니다")); // 메시지에 맞게 수정
    }

    @Test
    @DisplayName("주문 생성 시 잔액이 부족할 경우 예외가 발생한다")
    void 잔액_부족_예외() throws Exception {
        User user = userRepository.save(new User("잔액 부족 유저"));
        Product product = productRepository.findAll().get(0);
        balanceRepository.save(new Balance(user.getId(), 100)); // 아주 적은 잔액

        var orderRequest = new OrderRequest(user.getId(),
                List.of(new OrderItemRequest(product.getId(), 1, product.getPrice())),
                null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("잔액 부족"))
                .andExpect(jsonPath("$.data").value("잔액이 부족합니다."));// 메시지에 맞게 수정
    }

    @Test
    @DisplayName("주문 생성 시 상품의 상태가 삭제일 경우 예외가 발생한다")
    void 상품_삭제_예외() throws Exception {
        User user = userRepository.findAll().get(0);
        Product deletedProduct = productRepository.save(new Product("삭제 상품", 10000, ProductStatus.DELETE));
        productStockRepository.save(new ProductStock(deletedProduct, 10));

        var orderRequest = new OrderRequest(user.getId(),
                List.of(new OrderItemRequest(deletedProduct.getId(), 1, deletedProduct.getPrice())),
                null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("판매중인 상품이 아닙니다.")); // 메시지에 맞게 수정
    }

    @Test
    @DisplayName("동일 유저가 동시에 여러 주문 요청 시 하나만 성공해야 한다")
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

//   @DisplayName("주문 생성 후 재고 차감 성공 시 주문 상태는 COMFIRMED 이어야 한다")
//    @Test
//    void 주문_생성_후_재고_차감_성공_시_상태가_CONFIRMED() throws InterruptedException {
//
//        // given
//        Long userId = userRepository.findAll().get(0).getId();
//        Product product = productRepository.findAll().get(0);
//        int qty = 3;
//        var orderRequest = new OrderRequest(userId, List.of(new OrderItemRequest(product.getId(), qty, product.getPrice())), null);
//        OrderCommand command = OrderCommand.from(orderRequest);
//
//        // when
//        Order created = orderFacade.createOrder(command);
//        System.out.println("✅ created: " + created);
//        System.out.println("✅ userId = " + userId);
//        System.out.println("✅ productId = " + product.getId());
//        System.out.println("✅ command = " + command);
//
//        // then
//        // 일정 시간 대기 후 비동기 이벤트까지 처리되었는지 확인
//        Thread.sleep(100); // @Async나 EventQueue 처리 시간 고려
//        Order order = orderRepository.findById(created.getId()).get();
//        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
//
//    }

    @DisplayName("주문 생성 시 재고 차감이 성공하면 주문 상태는 CONFIRMED여야 한다")
    @Test
    void 주문_생성_및_재고차감_후_상태_CONFIRMED_확인() throws Exception {
        // given
        Long userId = userRepository.findAll().get(0).getId();
        Product product = productRepository.findAll().get(0);
        int qty = 3;

        OrderItemRequest itemRequest = new OrderItemRequest(product.getId(), qty, product.getPrice());
        OrderRequest orderRequest = new OrderRequest(userId, List.of(itemRequest), null);

        // when & then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.data.items[0].qty").value(qty))
                .andExpect(jsonPath("$.data.orderStatus").value("CONFIRMED"));
    }


    @DisplayName("주문 생성 후 상태는 PENDING → 이후 CONFIRMED로 변경되어야 한다")
    @Test
    void 주문_생성_이후_비동기_재고차감_으로_CONFIRMED_상태_전이_확인() throws Exception {
        // given
        Long userId = userRepository.findAll().get(0).getId();
        Product product = productRepository.findAll().get(0);
        int qty = 3;

        OrderItemRequest itemRequest = new OrderItemRequest(product.getId(), qty, product.getPrice());
        OrderRequest orderRequest = new OrderRequest(userId, List.of(itemRequest), null);

        // 주문 생성 요청
        String response = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderStatus").value("PENDING")) // ✅ 초기 상태 확인
                .andReturn()
                .getResponse()
                .getContentAsString();

        // ✅ 생성된 주문 ID 파싱
        JsonNode root = objectMapper.readTree(response);
        Long orderId = root.path("data").path("orderId").asLong();

        // ✅ Awaitility로 CONFIRMED 상태 될 때까지 대기
        await()
                .atMost(2, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    Order confirmed = orderRepository.findById(orderId).orElseThrow();
                    assertThat(confirmed.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
                });
    }

}
