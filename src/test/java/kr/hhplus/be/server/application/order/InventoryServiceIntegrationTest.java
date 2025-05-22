package kr.hhplus.be.server.application.order;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import kr.hhplus.be.server.application.payment.PaymentFacadeService;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductStockRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InventoryServiceIntegrationTest {

    @Autowired private OrderService           orderService;
    @Autowired private ProductRepository      productRepo;
    @Autowired private ProductStockRepository stockRepo;
    @Autowired private UserRepository         userRepository;
    @Autowired private BalanceRepository      balanceRepository;
    @Autowired private InventoryService       inventoryService;

    private Product p1, p2;
    private User user1, user2, user3;
    private Order order1, order2, order3;

    @BeforeEach
    void setUp() {
        p1 = productRepo.save(new Product("product1", 1_000, ProductStatus.AVAILABLE));
        p2 = productRepo.save(new Product("product2", 300_000, ProductStatus.ON_SALE));
        stockRepo.save(new ProductStock(p1, 100));
        stockRepo.save(new ProductStock(p2, 100));

        user1 = userRepository.save(new User("user1"));
        balanceRepository.save(new Balance(user1.getId(), 50_000));
        user2 = userRepository.save(new User("user2"));
        balanceRepository.save(new Balance(user2.getId(), 50_000));
        user3 = userRepository.save(new User("user3"));
        balanceRepository.save(new Balance(user3.getId(), 50_000));

        order1 = createAndPayOrder(user1, /*qty=*/10);
        order2 = createAndPayOrder(user2, /*qty=*/20);
        order3 = createAndPayOrder(user3, /*qty=*/5);
    }

    private Order createAndPayOrder(User user, int qty) {
        OrderItem item = new OrderItem(p1, qty, p1.getPrice());
        Order o = orderService.createPendingOrder(
                user,
                List.of(item),
                List.of(),
                qty * p1.getPrice()
        );
        // 결제 완료 → PaymentCompletedEvent 트리거
        //paymentFacadeService.completePayment(o.getId());
        return o;
    }

    @Test
    @DisplayName("결제 후 재고 차감이 정상적으로 실행된다")
    void 결제_이후_재고_차감_정상_동작() {
        inventoryService.decreaseStockWithDistributedLock(order1.getId(), List.of(p1.getId()));
        ProductStock stock = stockRepo.findByProductId(p1.getId()).orElseThrow();
        assertThat(stock.getStock()).isEqualTo(90);
    }

    @Test
    @DisplayName("여러 주문이 동시 실행되어도, 재고는 한 번씩만 정확히 차감되어야 한다")
    void 여러_주문_동시_재고_정합성() throws InterruptedException {
        // 준비: 3개의 스레드가 동시에 시작하도록 CountDownLatch 설정
        CountDownLatch ready = new CountDownLatch(3);
        CountDownLatch go    = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(3);

        Runnable task1 = () -> {
            ready.countDown();
            try {
                go.await();
                inventoryService.decreaseStockWithDistributedLock(order1.getId(), List.of(p1.getId()));
            } catch (Exception e) {
                // 락 획득 실패나 기타 오류 로깅
                System.out.printf("Thread-%s 예외: %s%n",
                        Thread.currentThread().getName(), e.getMessage());
            } finally {
                done.countDown();
            }
        };
        Runnable task2 = () -> { /* 동일하게 order2 */ ready.countDown(); try { go.await(); inventoryService.decreaseStockWithDistributedLock(order2.getId(), List.of(p1.getId())); } catch(Exception e){ } finally{ done.countDown(); } };
        Runnable task3 = () -> { /* 동일하게 order3 */ ready.countDown(); try { go.await(); inventoryService.decreaseStockWithDistributedLock(order3.getId(), List.of(p1.getId())); } catch(Exception e){ } finally{ done.countDown(); } };

        new Thread(task1, "T1").start();
        new Thread(task2, "T2").start();
        new Thread(task3, "T3").start();

        // 모두 준비되면 동시에 실행 → 완료 대기
        assertThat(ready.await(5, SECONDS)).isTrue();
        go.countDown();
        assertThat(done.await(10, SECONDS)).isTrue();

        // then: 초기 100 에서 10+20+5 만큼만 빠져야 한다
        ProductStock updated = stockRepo.findByProductId(p1.getId()).orElseThrow();
        assertThat(updated.getStock())
                .as("100 - (10+20+5) = 65")
                .isEqualTo(65);
    }

    @Test
    @DisplayName("두 주문이 서로 다른 순서로 중복된 상품 락을 잡아도 데드락 없이 재고가 정확히 차감되어야 한다")
    void 중복_상품_데드락_검증() throws InterruptedException {
        // 1) 첫 번째 주문: [p1 → p2]
        OrderItem oi1 = new OrderItem(p1, 10, p1.getPrice());
        OrderItem oi2 = new OrderItem(p2, 20, p2.getPrice());
        Order order1 = orderService.createPendingOrder(user1, List.of(oi1, oi2), List.of(), 10* p1.getPrice() + 20* p2.getPrice());

        // 2) 두 번째 주문: [p2 → p1]
        OrderItem oi3 = new OrderItem(p2, 15, p2.getPrice());
        OrderItem oi4 = new OrderItem(p1, 5,  p1.getPrice());
        Order order2 = orderService.createPendingOrder(user2, List.of(oi3, oi4), List.of(), 15* p2.getPrice() + 5* p1.getPrice());

        // 동시 실행을 위한 래치
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch go    = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(2);

        Runnable worker1 = () -> {
            ready.countDown();
            try {
                go.await(5, SECONDS);
                inventoryService.decreaseStockWithDistributedLock(order1.getId(), List.of(p1.getId(), p2.getId()));
            } catch (Exception ignore) {
            } finally {
                done.countDown();
            }
        };
        Runnable worker2 = () -> {
            ready.countDown();
            try {
                go.await(5, SECONDS);
                inventoryService.decreaseStockWithDistributedLock(order2.getId(), List.of(p2.getId(), p1.getId()));
            } catch (Exception ignore) {
            } finally {
                done.countDown();
            }
        };

        new Thread(worker1, "T1").start();
        new Thread(worker2, "T2").start();

        // 두 스레드가 준비되면 동시에 시작
        assertThat(ready.await(5, SECONDS)).isTrue();
        go.countDown();

        // 최대 10초 안에 두 작업 모두 끝나야 한다
        assertThat(done.await(10, SECONDS)).isTrue();

        // then: 최종 재고 검증
        ProductStock final1 = stockRepo.findByProductId(p1.getId()).orElseThrow();
        ProductStock final2 = stockRepo.findByProductId(p2.getId()).orElseThrow();

        // p1 은 100 → 100 − (10 + 5) = 85
        assertThat(final1.getStock()).isEqualTo(85);

        // p2 은 100 → 100 − (20 + 15) = 65
        assertThat(final2.getStock()).isEqualTo(65);
    }


}