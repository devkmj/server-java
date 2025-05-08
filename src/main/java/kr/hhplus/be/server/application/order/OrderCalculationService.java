package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.command.OrderItemCommand;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCalculationService {

    private final ProductService productService;
    private final ProductStockService productStockService;

    public OrderDto calculateOrderItems(List<OrderItemCommand> cmds, List<UserCoupon> userCoupons) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderItemCommand cmd : cmds) {
            Product p = productService.findByProductId(cmd.getProductId());
            ProductStock s = productStockService.findByProductId(cmd.getProductId());
            OrderItem item = OrderItem.fromCommand(cmd, p, s);
            items.add(item);
        }
        int subtotal = items.stream().mapToInt(OrderItem::getTotalPrice).sum();

        int discount = calculateDiscount(subtotal, userCoupons);
        return new OrderDto(
                items,
                subtotal,
                discount,
                subtotal - discount);
    }

    private int calculateDiscount(int subtotal, List<UserCoupon> userCoupons) {
        int totalDiscount = 0;
        for (UserCoupon uc : userCoupons) {
            uc.validateUsable();
            totalDiscount += subtotal * uc.getCoupon().getRate() / 100;
        }
        return Math.min(totalDiscount, subtotal);
    }
}
