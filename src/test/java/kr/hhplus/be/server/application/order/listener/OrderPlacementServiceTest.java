package kr.hhplus.be.server.application.order.listener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import kr.hhplus.be.server.application.order.OrderCalculationService;
import kr.hhplus.be.server.application.order.OrderPlacementService;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.domain.order.command.OrderCommand;

import kr.hhplus.be.server.domain.order.command.OrderItemCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import kr.hhplus.be.server.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class OrderPlacementServiceTest{

    @Mock private UserService userService;
    @Mock private UserCouponService userCouponService;
    @Mock private OrderCalculationService orderCalculationService;
    @Mock private OrderService orderService;

    @InjectMocks private OrderPlacementService placementService;

    @Nested
    @DisplayName("성공 케이스")
    class Success {

        @Test
        @DisplayName("유효한 커맨드가 주어지면 Order를 생성하여 저장하고 반환한다")
        void givenValidCommand_whenPlaceOrder_thenReturnsSavedOrder() {
            // --- Arrange ---
            OrderCommand cmd = OrderCommand.of(
                    1L,
                    List.of(new OrderItemCommand(100L, 2)),
                    List.of(10L, 20L)
            );

            // 1) User 조회
            User mockUser = new User(1L, "테스트유저");
            when(userService.findByUserId(1L)).thenReturn(mockUser);

            // 2) Coupon 조회
            List<UserCoupon> coupons = List.of();
            when(userCouponService.retrieveCoupons(cmd.getUserCouponIds()))
                    .thenReturn(coupons);

            // 3) 주문 계산
            Product mockProduct = new Product("테스트상품", 1000, ProductStatus.ON_SALE);
            int qty = cmd.getItems().get(0).getQty();  // 예: 2
            OrderItem orderItem = new OrderItem(mockProduct, qty, mockProduct.getPrice());
            List<OrderItem> orderItems = List.of(orderItem);
            OrderDto dto = new OrderDto(List.of(orderItem), 5000);
            when(orderCalculationService.calculateOrderItems(cmd.getItems()))
                    .thenReturn(dto);

            // 4) save 시 그대로 리턴
            when(orderService.save(any(Order.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // --- Act ---
            Order result = placementService.placeOrder(cmd);

            // --- Assert ---
            assertNotNull(result);
            assertEquals(mockUser, result.getUser());
            assertEquals(5000, result.getTotalPrice());

            verify(userService).findByUserId(1L);
            verify(userCouponService).retrieveCoupons(cmd.getUserCouponIds());
            verify(orderCalculationService).calculateOrderItems(cmd.getItems());
            verify(orderService).save(any(Order.class));
        }
    }

    @Nested
    @DisplayName("실패(예외) 케이스")
    class Failure {

        @Test
        @DisplayName("UserService 조회 실패 시 예외를 그대로 전파한다")
        void whenUserNotFound_thenThrows() {
            // --- Arrange ---
            OrderCommand cmd = mock(OrderCommand.class);
            when(cmd.getUserId()).thenReturn(1L);
            when(userService.findByUserId(1L))
                    .thenThrow(new RuntimeException("사용자 없음"));

            // --- Act & Assert ---
            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> placementService.placeOrder(cmd)
            );
            assertTrue(ex.getMessage().contains("사용자 없음"));

            verifyNoInteractions(userCouponService, orderCalculationService, orderService);
        }

        @Test
        @DisplayName("OrderCalculationService 실패 시 예외를 전파하고 save는 호출하지 않는다")
        void whenCalculationFails_thenThrows() {
            // --- Arrange ---
            OrderCommand cmd = OrderCommand.of(1L, List.of(), List.of());
            when(userService.findByUserId(anyLong()))
                    .thenReturn(new User(1L, "테스트"));
            when(userCouponService.retrieveCoupons(anyList()))
                    .thenReturn(List.of());
            when(orderCalculationService.calculateOrderItems(cmd.getItems()))
                    .thenThrow(new IllegalStateException("계산 실패"));

            // --- Act & Assert ---
            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> placementService.placeOrder(cmd)
            );
            assertEquals("계산 실패", ex.getMessage());

            verify(orderService, never()).save(any());
        }
    }
}
