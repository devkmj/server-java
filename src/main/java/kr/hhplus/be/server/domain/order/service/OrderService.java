package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order create(User user, List<OrderItem> orderItems, List<UserCoupon> userCoupons, int discountedTotal) {
        Order order = Order.create(user, userCoupons, orderItems, discountedTotal);
        return orderRepository.save(order);
    }

    public Order createPendingOrder(User user, List<OrderItem> orderItems, List<UserCoupon> userCoupons, int discountedTotal) {
        Order order = Order.create(user, userCoupons, orderItems, discountedTotal);
        return orderRepository.save(order);
    }
}
