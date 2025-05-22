package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.lock.RedissonLockService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final long LOCK_WAIT_SECONDS  = 5;
    private static final long LOCK_LEASE_SECONDS = 30;

    private final OrderService orderService;
    private final ProductStockService stockService;
    private final RedissonLockService redissonLockService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 지정된 주문의 모든 아이템에 대해 레디스 분산락을 걸고 재고 차감
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseStockWithDistributedLock(Long orderId, List<Long> productIds) {
        logger.info("order Id = " + orderId);
        List<String> keys = productIds.stream()
                .map(id -> "lock:product:" + id)
                .toList();
        logger.info("MultiLock key {}", keys);

        redissonLockService.lock(
                keys,
                (int) LOCK_WAIT_SECONDS,
                (int) LOCK_LEASE_SECONDS,
                TimeUnit.SECONDS,
                () -> {
                    doDecreaseStockInNewTx(orderId);
                    return null;
                }
        );
    }

    protected void doDecreaseStockInNewTx(Long orderId) {
        Order order = orderService.getOrder(orderId);
        logger.info("order Status = " + order.getStatus());
        order.getItems().forEach(item ->
                    stockService.decreaseStock(item.getProductId(), item.getQty())
        );
    }
}