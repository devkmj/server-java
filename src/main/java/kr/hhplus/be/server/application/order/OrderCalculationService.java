package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.model.OrderSummary;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.command.OrderItemCommand;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCalculationService {

    private final ProductService productService;
    private final ProductStockService productStockService;

    public OrderSummary calculateOrderItems(List<OrderItemCommand> itemCommands, Balance balance) {
        int totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemCommand cmd : itemCommands) {
            Product product = productService.findByProductId(cmd.getProductId());
            ProductStock stock = productStockService.findByProductId(cmd.getProductId());
            int qty = cmd.getQty();

            int itemTotalPrice = product.getPrice() * qty;
            totalPrice += itemTotalPrice;

            product.validateOrderable();
            stock.validateEnough(qty);
            balance.validateSufficient(itemTotalPrice);

            OrderItem orderItem = OrderItem.of(product, qty);
            orderItems.add(orderItem);
        }

        return new OrderSummary(orderItems, totalPrice);
    }
}
