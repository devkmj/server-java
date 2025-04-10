package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderValidator;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;

import kr.hhplus.be.server.domain.order.exception.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderFacade {
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;
    private final BalanceRepository balanceRepository;
    private final OrderValidator orderValidator;

    public OrderFacade(
            ProductRepository productRepository,
            ProductStockRepository productStockRepository,
            OrderRepository orderRepository,
            UserRepository userRepository,
            UserCouponRepository userCouponRepository,
            BalanceRepository balanceRepository,
            OrderValidator orderValidator
    ) {
        this.productRepository = productRepository;
        this.productStockRepository = productStockRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.userCouponRepository = userCouponRepository;
        this.balanceRepository = balanceRepository;
        this.orderValidator = orderValidator;
    }

    @Transactional
    public void order(Long userId, Long productId, int qty, Long userCouponId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new InsufficientStockException("상품 재고가 부족합니다."));
        Balance balance = balanceRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("잔액 정보가 없습니다."));

        UserCoupon userCoupon = null;
        int totalPrice = product.getPrice() * qty;

        if (userCouponId != null) {
            userCoupon = userCouponRepository.findById(userCouponId)
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
            totalPrice = userCoupon.getCoupon().discount(totalPrice);
        }

        // 검증
        orderValidator.validate(product, stock, balance, userCoupon, qty, totalPrice);

        // 상태 변경 (차감)
        balance.use(totalPrice);
        stock.decrease(qty);
        if (userCoupon != null) userCoupon.use();

        // 주문 생성 및 저장
        OrderItem orderItem = new OrderItem(product, qty, product.getPrice());
        Order order = Order.create(user, userCoupon, List.of(orderItem), totalPrice);
        orderRepository.save(order);
    }

}
