package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final OrderService orderService;
    private final ProductStockService stockService;

    /**
     * 지정된 주문의 모든 아이템에 대해 비관적 락을 걸고 재고 차감
     */
    @Transactional
    public void decreaseStockForOrder(Long orderId) {
        Order order = orderService.getOrder(orderId);
        order.getItems().forEach(item ->
                stockService.decreaseStock(item.getProductId(), item.getQty())
        );
    }

    /**
     * 재고 차감 성공 후 주문 상태를 CONFIRMED로 전이
     */
    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = orderService.getOrder(orderId);
        order.markAsConfirmed();
        orderService.save(order);
    }
}