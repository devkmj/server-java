package kr.hhplus.be.server.application.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.order.OrderItemRequest;
import kr.hhplus.be.server.api.order.OrderRequest;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Rollback(false)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Order 통합 테스트")
public class OrderFacadeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andExpect(jsonPath("$.data.usedCouponIds[0].id").value(userCoupon.getId()));
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
    @DisplayName("만료된 쿠폰 사용 시 예외 발생")
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
                .andExpect(jsonPath("$.data").value("유효하지 않은 쿠폰입니디"));
    }

    @Test
    @DisplayName("다른 유저의 쿠폰 사용 시 예외 발생")
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
    @DisplayName("재고 부족 시 예외 발생")
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
    @DisplayName("잔액 부족 시 예외 발생")
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
                .andExpect(jsonPath("$.data").value("잔액이 부족합니다"));// 메시지에 맞게 수정
    }

    @Test
    @DisplayName("상품 상태 삭제일 경우 예외 발생")
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
                .andExpect(jsonPath("$.data").value("판매중인 상품이 아닙니다")); // 메시지에 맞게 수정
    }
}
