package kr.hhplus.be.server.application.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.exception.InsufficientBalanceException;
import kr.hhplus.be.server.domain.order.exception.InsufficientStockException;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.annotation.Id;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@DisplayName("Order 도메인 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderFacadeTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductStockRepository productStockRepository;
    @Mock private BalanceRepository balanceRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserCouponRepository userCouponRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private OrderFacade orderFacade;

    @Test
    void 정상_주문_성공() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        Product product = new Product("테스트상품", 10000, ProductStatus.ON_SALE);
        ProductStock stock = new ProductStock(product, 10);
        Balance balance = new Balance(userId, 100000);
        User user = new User("테스트유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));

        // when & then
        assertThatCode(() -> orderFacade.order(userId, productId, qty, null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("재고 부족 시 예외가 발생한다")
    void 재고_부족_시_예외가_발생한다(){
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 3;
        Product product = new Product("맥북", 30000, ProductStatus.AVAILABLE);
        ProductStock stock = new ProductStock(product, 1); // 부족한 재고
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, null))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("상품 재고가 부족합니다");
    }

    @Test
    @DisplayName("잔액 부족 시 예외가 발생한다")
    void 잔액_부족_시_예외가_발생한다(){
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        Product product = new Product("맥북", price, ProductStatus.ON_SALE);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 5000); // 부족한 금액
        User user = new User("테스트유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, null))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessageContaining("잔액이 부족합니다");
    }

    @Test
    @DisplayName("쿠폰이 유효하지 않을 경우 예외가 발생한다")
    void 쿠폰이_유효하지_않을_경우_예외가_발생한다(){
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;
        Long userCouponId = 99L;

        Product product = new Product("맥북", price, ProductStatus.ON_SALE);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트유저");

        UserCoupon expiredCoupon = new UserCoupon(
                userCouponId,
                new Coupon(10, 100, 10, LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1))    // 유효기간 만료됨
        );

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(expiredCoupon));

        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty,  userCouponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 쿠폰입니다");
    }

    @Test
    @DisplayName("쿠폰이 이미 사용된 경우 예외가 발생한다")
    void 쿠폰이_이미_사용된_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        Long userCouponId = 1L;
        int qty = 1;

        Coupon coupon = new Coupon(10, 100, 10,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(7));
        UserCoupon userCoupon = new UserCoupon(userId, coupon);
        ReflectionTestUtils.setField(userCoupon, "id", userCouponId);
        userCoupon.use(); // 사용처리

        Product product = new Product("맥북", 320000, ProductStatus.AVAILABLE);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        // when & then
        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, userCouponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 사용된 쿠폰입니다");

    }

    @Test
    @DisplayName("상품 상태가 판매 중지일 경우 예외가 발생한다")
    void 상품_상태가_판매_중지일_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        Product product = new Product("맥북", 320000, ProductStatus.DISCONTINUED);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("판매중인 상품이 아닙니다.");
    }

    @Test
    @DisplayName("상품 상태가 삭제일 경우 예외가 발생한다")
    void 상품_상태가_삭제일_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        Product product = new Product("맥북", 320000, ProductStatus.DELETE);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("판매중인 상품이 아닙니다.");
    }

    @Test
    @DisplayName("상품 상태가 품절일 경우 예외가 발생한다")
    void 상품_상태가_품절일_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        Product product = new Product("맥북", 320000, ProductStatus.SOLD_OUT);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));

        // when & then
        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("판매중인 상품이 아닙니다.");
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 ID일 경우 예외가 발생한다")
    void 존재하지_않는_쿠폰_ID일_경우_예외() {
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        Long userCouponId = 1L;

        Product product = new Product("맥북", 320000, ProductStatus.SOLD_OUT);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productStockRepository.findByProductId(productId)).willReturn(Optional.of(stock));
        given(balanceRepository.findByUserId(userId)).willReturn(Optional.of(balance));
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, userCouponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠폰을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID일 경우 예외가 발생한다")
    void 존재하지_않는_사용자_ID일_경우_예외() {
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;

        Product product = new Product("맥북", 30000, ProductStatus.AVAILABLE);
        ProductStock stock = new ProductStock(product, 5);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderFacade.order(userId, productId, qty, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID일 경우 예외가 발생한다")
    void 존재하지_않는_상품_ID일_경우_예외() {
        Long userId = 1L;
        Long productId = 99L;
        User user = new User("테스트 유저");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderFacade.order(userId, productId, 1, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 상품입니다");
    }
}
