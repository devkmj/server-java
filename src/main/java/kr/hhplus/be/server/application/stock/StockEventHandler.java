package kr.hhplus.be.server.application.stock;

import kr.hhplus.be.server.domain.balance.event.RefundBalanceEvent;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.event.StockDecreaseRequestedEvent;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.domain.user.event.RestoreUserCouponEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.context.ApplicationEventPublisher;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockEventHandler {

    private final OrderRepository orderRepository;
    private final ProductStockService productStockService;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void handle(StockDecreaseRequestedEvent event) {
        log.info("[EVENT][재고 차감 요청] 주문 ID: {}", event.getOrderId());
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 존재하지 않습니다."));
        try {
            // 재고 차감
            productStockService.decreaseProductStocks(order.getItems());
            // 성공 시 주문 상태 전이
            order.markAsConfirmed();
            log.info("[EVENT][재고 차감 성공] 주문 ID: {}", event.getOrderId());
        } catch (IllegalStateException e) {
            // 재고 차감 실패 시 주문 실패 처리
            order.markAsFailed("재고 차감 실패");
            log.warn("[EVENT][재고 차감 실패] 주문 ID: {}, 에러: {}", event.getOrderId(), e.getMessage());
            // 쿠폰, 잔액 반환 처리
            eventPublisher.publishEvent(new RefundBalanceEvent(order.getUser(), order.getTotalPrice()));
            eventPublisher.publishEvent(new RestoreUserCouponEvent(order.getUserCoupons()));
        }
    }
}
