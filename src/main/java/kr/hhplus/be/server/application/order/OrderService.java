package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderItemCommand;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(User user, List<OrderItem> items, List<UserCoupon> coupons, int totalPrice) {
        Order order = Order.create(user, coupons, items, totalPrice);
        return orderRepository.save(order);
    }

}
