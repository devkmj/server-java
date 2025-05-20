package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.service.InventoryService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.lock.RedissonLockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    private static final long ORDER_ID = 100L;
    private static final long PRODUCT_ID_1 = 10L;
    private static final long PRODUCT_ID_2 = 5L;
    private static final int QTY_1 = 2;
    private static final int QTY_2 = 3;
    private static final int LOCK_WAIT = 5;
    private static final int LOCK_LEASE = 30;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductStockService stockService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RedissonLockService lockService;

    @Mock
    private RLock multiLock;

    @InjectMocks
    private InventoryService inventoryService;

    private Order order;

    @BeforeEach
    void setUp() {
        // 주문에 두 개의 아이템이 들어있는 Order 객체를 준비
        User user = new User("testUser");
        order = Order.of(user);
        order.addItem(mockOrderItem(PRODUCT_ID_1, QTY_1));
        order.addItem(mockOrderItem(PRODUCT_ID_2, QTY_2));

        given(orderService.getOrder(ORDER_ID)).willReturn(order);
    }

    @Test
    @DisplayName("락 획득 성공 시 모든 상품에 대해 재고차감 호출")
    void decreaseStockWithLock_success() {
        // multiLock이 호출되면 Supplier.get()만 실행하도록 스텁
        willAnswer(inv -> {
            @SuppressWarnings("unchecked")
            Supplier<Void> action = inv.getArgument(4, Supplier.class);
            return action.get();
        }).given(lockService).lock(
                anyList(), eq(LOCK_WAIT), eq(LOCK_LEASE), eq(SECONDS), any(Supplier.class)
        );

        // when
        inventoryService.decreaseStockWithDistributedLock(ORDER_ID, List.of(PRODUCT_ID_1, PRODUCT_ID_2));

        // then
        then(stockService).should().decreaseStock(PRODUCT_ID_1, QTY_1);
        then(stockService).should().decreaseStock(PRODUCT_ID_2, QTY_2);

        // 락 서비스가 올바른 키 리스트와 파라미터로 호출됐는지 검증
        then(lockService).should().lock(
                argThat(keys -> keys.containsAll(List.of("lock:product:10", "lock:product:5"))),
                eq(LOCK_WAIT), eq(LOCK_LEASE), eq(SECONDS), any(Supplier.class)
        );
    }

    @Test
    @DisplayName("락 획득 실패 시 예외 발생, 재고차감 호출 없음")
    void decreaseStockWithLock_lockFail() {
        given(lockService.lock(
                anyList(),
                anyInt(),
                anyInt(),
                any(TimeUnit.class),
                any(Supplier.class)
        )).willThrow(new IllegalStateException("락 획득 실패"));

        assertThatThrownBy(() ->
                inventoryService.decreaseStockWithDistributedLock(ORDER_ID, List.of(PRODUCT_ID_1, PRODUCT_ID_2))
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("락 획득 실패");

        verify(stockService, never()).decreaseStock(anyLong(), anyInt());
    }

    private OrderItem mockOrderItem(Long productId, int qty) {
        OrderItem item = Mockito.mock(OrderItem.class);
        given(item.getProductId()).willReturn(productId);
        given(item.getQty()).willReturn(qty);
        return item;
    }

}
