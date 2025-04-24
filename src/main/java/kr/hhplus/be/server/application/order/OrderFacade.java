package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.event.StockDecreaseRequestedEvent;
import kr.hhplus.be.server.domain.product.service.ProductStockService;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import kr.hhplus.be.server.domain.user.service.UserService;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.balance.entity.Balance;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final ProductStockService productStockService;
    private final BalanceService balanceService;
    private final UserCouponService userCouponService;
    private final OrderService orderService;
    private final OrderCalculationService orderCalculationService;
    private final CouponService couponService;
    private final ApplicationEventPublisher eventPublisher;

//    @Transactional
//    public Order createOrder_old(OrderCommand command) {
//        // 1. 입력 검증 및 초기 데이터 조회
//        User user = userService.findByUserId(command.getUserId());
//        Balance balance = balanceService.findByUserId(command.getUserId());
//
//        // 2. 주문 계산 처리
//        OrderDto calcResult = orderCalculationService.calculateOrderItems(command.getItems(), balance);
//
//        // 3. 쿠폰 할인 처리
//        List<UserCoupon> userCoupons = userCouponService.retrieveCoupons(command.getUserCouponIds());
//        int discountedTotal = couponService.applyCoupons(userCoupons, calcResult.getTotalPrice());
//
//        // 4. 상태 변경 (잔액 차감, 상품 재고 차감, 사용자 쿠폰 사욪 처리)
//        balanceService.useBalance(balance, discountedTotal);
//        //productStockService.decreaseProductStocks(command.getItems());
//        userCouponService.useUserCoupons(userCoupons);
//
//        // 5. 주문 생성 및 저장
//        Order createdOrder = orderService.create(user, calcResult.getOrderItems(), userCoupons, discountedTotal);
//        return createdOrder;
//    }

    @Transactional
    public Order createOrder(OrderCommand command) {
        // 1. 입력 검증 및 초기 조회
        User user = userService.findByUserId(command.getUserId());
        Balance balance = balanceService.findByUserId(command.getUserId());

        // 2. 주문 계산
        OrderDto calcResult = orderCalculationService.calculateOrderItems(command.getItems(), balance);

        // 3. 쿠폰 할인
        List<UserCoupon> userCoupons = userCouponService.retrieveCoupons(command.getUserCouponIds());
        int discountedTotal = couponService.applyCoupons(userCoupons, calcResult.getTotalPrice());

        // 4. 즉시 처리: 쿠폰 사용 + 잔액 차감
        userCouponService.useUserCoupons(userCoupons);
        balanceService.useBalance(balance, discountedTotal);

        // 5. 주문 생성 (PENDING 상태)
        Order createdOrder = orderService.createPendingOrder(user, calcResult.getOrderItems(), userCoupons, discountedTotal);

        // 6. 재고 차감 요청 이벤트 발행
        eventPublisher.publishEvent(new StockDecreaseRequestedEvent(createdOrder.getId()));

        return createdOrder;
    }

}
