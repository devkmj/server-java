package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Order 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 성공")
    void 주문이_성공적으로_생성된다() {
        // given
        User user = new User("테스트유저");
        Product product = new Product("맥북", 1000000, ProductStatus.ON_SALE);
        int qty = 2;
        int totalPrice = product.getPrice() * qty;

        OrderItem orderItem = new OrderItem(product, qty, product.getPrice());

        // when
        Order order = orderService.createOrder(user, List.of(orderItem), null, totalPrice);

        // then
        assertThat(order).isNotNull();
        assertThat(order.getOrderItems()).hasSize(1);
        assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
    }


    @Test
    @DisplayName("주문 아이템이 없을 경우 예외 처리")
    void 주문_아이템이_없을_경우_예외_처리된다(){
        // given
        User user = new User("테스트유저");
        // when

        // then
    }
}
