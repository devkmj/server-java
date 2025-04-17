package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.user.User;
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
