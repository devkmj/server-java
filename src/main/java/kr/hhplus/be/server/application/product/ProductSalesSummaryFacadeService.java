package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.service.ProductSalesSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProductSalesSummaryFacadeService {

    private final OrderService orderService;
    private final ProductSalesSummaryService productSalesSummaryService;

    @Transactional
    public void updateProductSalesSummary(Long orderId) {
        Order order = orderService.getOrder(orderId);
        LocalDate today = LocalDate.now();
        order.getOrderItems().forEach(item -> {
            productSalesSummaryService.addSalesSummary(item.getProduct(), item.getQty(), today);
        });
    }
}
