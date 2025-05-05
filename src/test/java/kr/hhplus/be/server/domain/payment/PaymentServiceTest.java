package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Payment 단위 테스트")
@ExtendWith(SpringExtension.class)
public class PaymentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher publisher;

    @InjectMocks private PaymentService paymentService;


    private Order createPendingOrder(int totalPrice) {
        User mockUser = new User(1L, "테스트유저");
        when(userService.findByUserId(1L)).thenReturn(mockUser);

        Product dummy = new Product("더미상품", totalPrice, ProductStatus.ON_SALE);
        OrderItem item = new OrderItem(dummy, 1, dummy.getPrice());

        // 3) 주문 생성
        return Order.createPending(
                mockUser,
                List.of(item),
                Collections.emptyList(),
                totalPrice
        );
    }

    @Test
    @DisplayName("applyPayment: 성공 시 쿠폰 사용, 잔액 차감, 상태 PAID")
    void applyPayment_Success() {
        // given
        Order order = createPendingOrder(1_000);
        UserCoupon c1 = mock(UserCoupon.class);
        UserCoupon c2 = mock(UserCoupon.class);
        List<UserCoupon> coupons = List.of(c1, c2);
        // 충분한 잔액 세팅
        Balance balance = new Balance(order.getUser().getId(), 2_000);

        // when
        paymentService.applyPayment(order, coupons, balance);

        // then
        // 1) 각각의 쿠폰 use() 호출 검증
        verify(c1).use();
        verify(c2).use();
        // 2) 잔액이 totalPrice 만큼 차감됐는지
        assertThat(balance.getBalance()).isEqualTo(1_000);
        // 3) 주문 상태가 PAID로 전이됐는지
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("applyPayment: 쿠폰 사용 중 예외 발생 시 전체 롤백")
    void applyPayment_CouponUseFails_Rollback() {
        // given
        Order order = createPendingOrder(500);
        UserCoupon c1 = mock(UserCoupon.class);
        doThrow(new IllegalStateException("coupon error")).when(c1).use();
        List<UserCoupon> coupons = List.of(c1);
        Balance balance = new Balance(order.getUser().getId(), 1_000);

        // when & then
        assertThatThrownBy(() ->
                paymentService.applyPayment(order, coupons, balance)
        ).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("coupon error");

        // 쿠폰 예외 시잔액·상태 변경 없어야 함
        assertThat(balance.getBalance()).isEqualTo(1_000);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("applyPayment: 잔액 부족 시 예외 발생 및 롤백")
    void applyPayment_InsufficientBalance_Rollback() {
        // given
        int total = 2_000;
        Order order = createPendingOrder(total);
        List<UserCoupon> coupons = Collections.emptyList();
        // 부족한 잔액
        Balance balance = new Balance(order.getUser().getId(), 1_000);

        // when & then
        assertThatThrownBy(() ->
                paymentService.applyPayment(order, coupons, balance)
        ).isInstanceOf(RuntimeException.class);  // 실제 Balance.deduct()가 던지는 예외 타입으로 교체

        // 상태·잔액 변경 없어야 함
        assertThat(balance.getBalance()).isEqualTo(1_000);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }
}
