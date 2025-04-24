package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.order.command.OrderItemCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.event.StockDecreaseRequestedEvent;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import kr.hhplus.be.server.domain.user.service.UserService;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.service.OrderItemService;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.order.exception.InsufficientStockException;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.interfaces.api.order.request.OrderItemRequest;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@DisplayName("Order 도메인 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;

    @Mock private OrderCalculationService orderCalculationService;
    @Mock private UserService userService;
    @Mock private ProductService productService;
    @Mock private ProductStockService productStockService;
    @Mock private BalanceService balanceService;
    @Mock private UserCouponService userCouponService;
    @Mock private OrderService orderService;
    @Mock private CouponService couponService;
    @Mock private OrderItemService orderItemService;
    @Mock private ApplicationEventPublisher eventPublisher;

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
        OrderItem orderItem = new OrderItem(product, qty, product.getPrice());
        OrderDto summary = new OrderDto(List.of(orderItem), product.getPrice() * qty);

        OrderCommand command = OrderCommand.of(
                userId,
                List.of(new OrderItemCommand(productId, qty)),
                null);

        given(userService.findByUserId(userId)).willReturn(user);
        given(balanceService.findByUserId(userId)).willReturn(balance);
        given(orderCalculationService.calculateOrderItems(anyList(), eq(balance))).willReturn(summary);

        Order order = mock(Order.class);
        given(order.getId()).willReturn(100L); // mock 반환값 설정
        given(orderService.createPendingOrder(any(User.class), anyList(), anyList(), anyInt()))
                .willReturn(order);

        // when
        Order result = orderFacade.createOrder(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
    }

//    @Test
//    void 재고가_부족하면_예외가_발생한다() {
//        // given
//        Long userId = 1L;
//        Long productId = 100L;
//        int qty = 5;
//        int productPrice = 2000;
//
//        OrderItemCommand item = new OrderItemCommand(productId, qty);
//        OrderCommand command = new OrderCommand(userId, List.of(item), null); // userCouponIds 생략 가능
//
//        User mockUser = new User(userId, "사용자");
//        Balance mockBalance = new Balance(userId, 10000);
//
//        Product mockProduct = new Product("상품", productPrice, ProductStatus.AVAILABLE); // 재고 10 가정
//        OrderItem orderItem = new OrderItem(mockProduct, qty, productPrice);
//        OrderDto summary = new OrderDto(List.of(orderItem), orderItem.getTotalPrice());
//
//        when(userService.findByUserId(userId)).thenReturn(mockUser);
//        when(balanceService.findByUserId(userId)).thenReturn(mockBalance);
//        when(orderCalculationService.calculateOrderItems(anyList(), any())).thenReturn(summary);
//        when(couponService.applyCoupons(anyList(), anyInt())).thenReturn(orderItem.getTotalPrice());
//
//        // 상품 재고 감소 시 예외 발생
//        doThrow(new InsufficientStockException("재고가 부족합니다"))
//                .when(productStockService)
//                .decreaseProductStocks(List.of(item));
//
//        // when & then
//        InsufficientStockException exception = assertThrows(
//                InsufficientStockException.class,
//                () -> orderFacade.createOrder(command)
//        );
//
//        assertEquals("재고가 부족합니다", exception.getMessage());
//    }

    @Test
    @DisplayName("주문 생성 시 잔액이 부족할 경우 예외가 발생한다")
    void 잔액_부족_시_예외가_발생한다(){
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 5;
        int productPrice = 3000;
        int totalPrice = productPrice * qty;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), null);

        Product mockProduct = new Product("상품", productPrice, ProductStatus.AVAILABLE);
        OrderItem orderItem = new OrderItem(mockProduct, qty, productPrice);
        OrderDto summary = new OrderDto(List.of(orderItem), orderItem.getTotalPrice());
        Balance mockBalance = new Balance(userId, 1000);

        when(orderCalculationService.calculateOrderItems(anyList(), any())).thenReturn(summary);
        when(balanceService.findByUserId(userId)).thenReturn(mockBalance);

        doThrow(new IllegalArgumentException("잔액이 부족합니다"))
                .when(balanceService)
                .useBalance(eq(mockBalance), anyInt());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderFacade.createOrder(command)
        );

        assertEquals("잔액이 부족합니다", exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성 시 쿠폰이 유효하지 않을 경우 예외가 발생한다")
    void 쿠폰이_유효하지_않을_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 2;
        int productPrice = 5000;
        int totalPrice = qty * productPrice;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), List.of(123L)); // 잘못된 쿠폰 ID 포함

        Product mockProduct = new Product("상품", productPrice, ProductStatus.AVAILABLE);
        OrderItem orderItem = new OrderItem(mockProduct, qty, productPrice);
        OrderDto summary = new OrderDto(List.of(orderItem), totalPrice);

        when(userService.findByUserId(userId)).thenReturn(new User(userId, "사용자"));
        when(balanceService.findByUserId(userId)).thenReturn(new Balance(userId, 20000));
        when(orderCalculationService.calculateOrderItems(anyList(), any())).thenReturn(summary);

        when(couponService.applyCoupons(anyList(), anyInt()))
                .thenThrow(new IllegalArgumentException("유효하지 않은 쿠폰입니다"));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderFacade.createOrder(command)
        );

        assertEquals("유효하지 않은 쿠폰입니다", exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성 시 쿠폰이 이미 사용된 경우 예외가 발생한다")
    void 쿠폰이_이미_사용된_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 2;
        int productPrice = 5000;
        int totalPrice = qty * productPrice;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), List.of(123L)); // 잘못된 쿠폰 ID 포함

        Product mockProduct = new Product("상품", productPrice, ProductStatus.AVAILABLE);
        OrderItem orderItem = new OrderItem(mockProduct, qty, productPrice);
        OrderDto summary = new OrderDto(List.of(orderItem), totalPrice);
        Coupon coupon = new Coupon(30, 1000, 200, LocalDateTime.now().minusMonths(1) ,LocalDateTime.now().plusDays(1));
        UserCoupon usedCoupon = new UserCoupon(userId, coupon);
        usedCoupon.markAsUsed(); // 사용된 상태로 변경

        when(userService.findByUserId(userId)).thenReturn(new User(userId, "사용자"));
        when(balanceService.findByUserId(userId)).thenReturn(new Balance(userId, 20000));
        when(orderCalculationService.calculateOrderItems(anyList(), any())).thenReturn(summary);

        when(couponService.applyCoupons(anyList(), anyInt()))
                .thenThrow(new IllegalArgumentException("이미 사용된 쿠폰입니다"));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderFacade.createOrder(command)
        );

        assertEquals("이미 사용된 쿠폰입니다", exception.getMessage());

    }

    @Test
    @DisplayName("주문 생성 시 상품 상태가 판매 중지일 경우 예외가 발생한다")
    void 상품_상태가_판매_중지일_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), List.of(123L));

        Product product = new Product("맥북", 320000, ProductStatus.DISCONTINUED);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userService.findByUserId(userId)).willReturn(user);
        when(orderCalculationService.calculateOrderItems(anyList(), any()))
                .thenThrow(new IllegalArgumentException("판매중인 상품이 아닙니다"));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderFacade.createOrder(command)
        );

        assertEquals("판매중인 상품이 아닙니다", exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성 시 상품 상태가 삭제일 경우 예외가 발생한다")
    void 상품_상태가_삭제일_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), List.of(123L));

        Product product = new Product("맥북", 320000, ProductStatus.DELETE);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userService.findByUserId(userId)).willReturn(user);
        when(orderCalculationService.calculateOrderItems(anyList(), any()))
                .thenThrow(new IllegalArgumentException("판매중인 상품이 아닙니다"));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderFacade.createOrder(command)
        );

        assertEquals("판매중인 상품이 아닙니다", exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성 시 상품 상태가 품절일 경우 예외가 발생한다")
    void 상품_상태가_품절일_경우_예외가_발생한다() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), List.of(123L));

        Product product = new Product("맥북", 320000, ProductStatus.SOLD_OUT);
        ProductStock stock = new ProductStock(product, 5);
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userService.findByUserId(userId)).willReturn(user);
        when(orderCalculationService.calculateOrderItems(anyList(), any()))
                .thenThrow(new IllegalArgumentException("판매중인 상품이 아닙니다"));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderFacade.createOrder(command)
        );

        assertEquals("판매중인 상품이 아닙니다", exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성 시 존재하지 않는 쿠폰 ID일 경우 예외가 발생한다")
    void 존재하지_않는_쿠폰_ID일_경우_예외() {
        Long userId = 1L;
        Long productId = 1L;
        Long userCouponId = 1L;
        int qty = 1;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), List.of(123L));
        Product product = new Product("맥북", 320000, ProductStatus.SOLD_OUT);
        OrderItem orderItem = new OrderItem(product, qty, product.getPrice());

        OrderDto summary = new OrderDto(List.of(orderItem), product.getPrice() * qty);

        when(userService.findByUserId(userId)).thenReturn(new User(userId, "사용자"));
        when(balanceService.findByUserId(userId)).thenReturn(new Balance(userId, 20000));
        when(orderCalculationService.calculateOrderItems(anyList(), any())).thenReturn(summary);

        when(couponService.applyCoupons(anyList(), anyInt()))
                .thenThrow(new IllegalArgumentException("쿠폰을 찾을 수 없습니다"));

        assertThatThrownBy(() -> orderFacade.createOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠폰을 찾을 수 없습니다");
    }


    @Test
    @DisplayName("주문 생성 시 존재하지 않는 사용자 ID일 경우 예외가 발생한다")
    void 존재하지_않는_사용자_ID일_경우_예외() {
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;

        Product product = new Product("맥북", 30000, ProductStatus.AVAILABLE);

        given(userService.findByUserId(userId))
                .willThrow(new IllegalArgumentException("존재하지 않는 사용자입니다"));

        OrderCommand command = OrderCommand.of(userId, List.of(new OrderItemCommand(productId, qty)), null);

        assertThatThrownBy(() -> orderFacade.createOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    @DisplayName("주문 생성 시 존재하지 않는 상품 ID일 경우 예외가 발생한다")
    void 존재하지_않는_상품_ID일_경우_예외() {
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;
        int price = 10000;

        OrderItemCommand item = new OrderItemCommand(productId, qty);
        OrderCommand command = new OrderCommand(userId, List.of(item), List.of(123L));
        Balance balance = new Balance(userId, 500000);
        User user = new User("테스트 유저");

        given(userService.findByUserId(userId)).willReturn(user);
        when(orderCalculationService.calculateOrderItems(anyList(), any()))
                .thenThrow(new IllegalArgumentException("존재하지 않는 상품입니다."));

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderFacade.createOrder(command)
        );

        assertEquals("존재하지 않는 상품입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 생성시 재고 차감 이벤트가 발행된다")
    void 주문_생성시_이벤트가_발행된다(){
        // given
        Long userId = 1L;
        Long productId = 1L;
        int qty = 1;

        Product product = new Product("테스트상품", 10000, ProductStatus.ON_SALE);
        ProductStock stock = new ProductStock(product, 10);
        Balance balance = new Balance(userId, 100000);
        User user = new User("테스트유저");
        OrderItem orderItem = new OrderItem(product, qty, product.getPrice());
        OrderDto summary = new OrderDto(List.of(orderItem), product.getPrice() * qty);

        OrderCommand command = OrderCommand.of(
                userId,
                List.of(new OrderItemCommand(productId, qty)),
                null);

        given(userService.findByUserId(userId)).willReturn(user);
        given(balanceService.findByUserId(userId)).willReturn(balance);
        given(orderCalculationService.calculateOrderItems(anyList(), eq(balance))).willReturn(summary);

        Order order = mock(Order.class);
        given(order.getId()).willReturn(100L); // mock 반환값 설정
        given(orderService.createPendingOrder(any(User.class), anyList(), anyList(), anyInt()))
                .willReturn(order);

        // when
        Order result = orderFacade.createOrder(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(eventPublisher).publishEvent(any(StockDecreaseRequestedEvent.class));
    }

}
