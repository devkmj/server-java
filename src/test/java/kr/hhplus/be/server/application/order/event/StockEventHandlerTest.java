package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.application.stock.StockEventHandler;
import kr.hhplus.be.server.domain.balance.event.RefundBalanceEvent;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.event.StockDecreaseRequestedEvent;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.event.RestoreUserCouponEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("재고 차감 이벤트 단위 테스트")
@ExtendWith(MockitoExtension.class)
class StockEventHandlerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductStockService productStockService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private StockEventHandler stockEventHandler;

    @DisplayName("재고 차감 실패 시 보상 이벤트가 발행된다")
    @Test
    void 재고_차감_실패_시_보상_이벤트_발생() {
        // given
        User user = new User("TEST USER");
        Product product = new Product("맥북", 1000000, ProductStatus.ON_SALE);
        int qty = 10;
        int totalPrice = product.getPrice() * qty;

        OrderItem orderItem = new OrderItem(product, qty, product.getPrice());
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);

        List<UserCoupon> userCoupons = new ArrayList<>(); // 실제로 빈 리스트도 OK

        Order order = Order.create(user, userCoupons, orderItems, totalPrice);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doThrow(new IllegalStateException("재고 차감 실패"))
                .when(productStockService).decreaseProductStocks(any());

        // when
        stockEventHandler.handle(new StockDecreaseRequestedEvent(1L));

        // then
        verify(eventPublisher).publishEvent(any(RefundBalanceEvent.class));
        verify(eventPublisher).publishEvent(any(RestoreUserCouponEvent.class));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
    }
}