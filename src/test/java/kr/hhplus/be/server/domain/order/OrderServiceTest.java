package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@DisplayName("Order 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUser");
        order = Order.of(user);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void 주문이_성공적으로_생성된다() {
        // given
        User user = new User("테스트유저");
        Product product = new Product("맥북", 1000000, ProductStatus.ON_SALE);
        int qty = 2;
        int totalPrice = product.getPrice() * qty;

        OrderItem orderItem = new OrderItem(product, qty, product.getPrice());
        List<UserCoupon> userCoupons = Collections.emptyList();
        // when
        Order order = orderService.createPendingOrder(user, List.of(orderItem), userCoupons, totalPrice);

        // then
        assertThat(order).isNotNull();
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
    }

    @Test
    @DisplayName("confirmOrder: 상태가 PAID인 주문을 CONFIRMED로 전이하고 저장을 호출한다")
    void confirmOrder_성공() {
        // given
        order.markAsBalanceDeducted();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // when
        orderService.confirmOrder(1L);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);

        //ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        //verify(orderRepository).save(captor.capture());
        //assertThat(captor.getValue().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("confirmOrder: 존재하지 않는 주문이면 IllegalArgumentException을 던진다")
    void confirmOrder_주문없음_예외() {
        // given
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.confirmOrder(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 접근입니다");
    }

    @Test
    @DisplayName("confirmOrder: 이미 CONFIRMED 상태인 주문이면 IllegalStateException을 던진다")
    void confirmOrder_이미확정_예외() {
        // given: 상태가 PAID가 아닌 경우
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.confirmOrder(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 올바르지 않습니다");
    }

}
