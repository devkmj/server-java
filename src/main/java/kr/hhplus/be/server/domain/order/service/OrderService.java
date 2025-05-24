package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createPendingOrder(User user, List<OrderItem> orderItems, List<UserCoupon> userCoupons, int discountedTotal) {
        Order order = Order.createPending(user, orderItems, userCoupons, discountedTotal);
        return orderRepository.save(order);
    }

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("잘못된 접근입니다."));
    }

    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("잘못된 접근입니다."));
        order.markAsConfirmed();
        orderRepository.save(order);
    }

    @Transactional
    public void markAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new IllegalArgumentException("잘못된 접근입니다."));
        order.markAsBalanceDeducted();
        orderRepository.save(order);
    }

}
