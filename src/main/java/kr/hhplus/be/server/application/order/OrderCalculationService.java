package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.domain.order.model.OrderValidator;
import kr.hhplus.be.server.domain.order.command.OrderItemCommand;
import kr.hhplus.be.server.domain.product.ProductStockService;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStock;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderCalculationService {

    private final ProductService productService;
    private final ProductStockService productStockService;
    private final OrderValidator orderValidator;

    public OrderCalculationService(
            ProductService productService,
            ProductStockService productStockService,
            OrderValidator orderValidator
    ) {
        this.productService = productService;
        this.productStockService = productStockService;
        this.orderValidator = orderValidator;
    }

    /**
     * 각 주문 아이템(Command)으로부터 OrderItem 엔티티를 생성하고 누적 총액을 계산합니다.
     * 개별 아이템에 대한 검증 로직도 내부에서 처리합니다.
     */
    public OrderSummary calculateOrderItems(List<OrderItemCommand> itemCommands, Balance balance) {
        int totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemCommand cmd : itemCommands) {
            // 각 주문 아이템에 대해 상품과 재고 조회
            Product product = productService.findByProductId(cmd.getProductId());
            ProductStock stock = productStockService.findByProductId(cmd.getProductId());
            int qty = cmd.getQty();
            int itemTotalPrice = product.getPrice() * qty;
            totalPrice += itemTotalPrice;

            // 개별 상품에 대한 기본 검증 (예: 재고 부족)
            orderValidator.validate(product, stock, balance, null, qty, itemTotalPrice);

            // 주문 아이템 엔티티 생성
            OrderItem orderItem = new OrderItem(product, qty, product.getPrice());
            orderItems.add(orderItem);
        }

        return new OrderSummary(orderItems, totalPrice);
    }
}
