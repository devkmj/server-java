package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.product.ProductStockService;
import kr.hhplus.be.server.application.order.dto.*;
import kr.hhplus.be.server.application.user.UserCouponService;
import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.application.balance.BalanceService;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.domain.order.OrderValidator;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.balance.Balance;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderFacade {

    private final UserService userService;
    private final ProductStockService productStockService;
    private final BalanceService balanceService;
    private final UserCouponService userCouponService;
    private final OrderService orderService;
    private final OrderCalculationService orderCalculationService;
    private final CouponService couponService;

    public OrderFacade(
            OrderValidator orderValidator,
            UserService userService,
            ProductService productService,
            ProductStockService productStockService,
            BalanceService balanceService,
            UserCouponService userCouponService,
            OrderService orderService,
            OrderItemService orderItemService,
            OrderCalculationService orderCalculationService,
            CouponService couponService
    ) {
        this.userService = userService;
        this.productStockService = productStockService;
        this.balanceService = balanceService;
        this.userCouponService = userCouponService;
        this.orderService = orderService;
        this.orderCalculationService = orderCalculationService;
        this.couponService = couponService;
    }

    @Transactional
    public OrderDto order(OrderCommand command) {
        // 1. 입력 검증 및 초기 데이터 조회
        User user = userService.findByUserId(command.getUserId());
        Balance balance = balanceService.findByUserId(command.getUserId());

        // 2. 주문 계산 처리
        OrderSummary calcResult = orderCalculationService.calculateOrderItems(command.getItems(), balance);

        // 3. 쿠폰 할인 처리
        List<UserCoupon> coupons = couponService.retrieveCoupons(command.getUserCouponIds());
        int discountedTotal = couponService.applyCoupons(coupons, calcResult.getTotalPrice());

        // 4. 상태 변경 (잔액 차감, 상품 재고 차감, 사용자 쿠폰 사욪 처리)
        balanceService.useBalance(balance, discountedTotal);
        productStockService.decreaseProductStocks(command.getItems());
        userCouponService.useUserCoupons(coupons);

        // 5. 주문 생성 및 저장 (도메인 객체 내 캡슐화)
        Order order = orderService.createOrder(user, calcResult.getOrderItems(), coupons, discountedTotal);

        // 6. 응답 매핑
        return OrderMapper.toOrderDto(order);
    }

}
