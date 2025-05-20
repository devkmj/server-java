package kr.hhplus.be.server.domain.payment.service;

import org.springframework.transaction.annotation.Transactional;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Transactional
    public void applyPayment(
            Order order,
            List<UserCoupon> userCoupons,
            Balance balance
    ) {
        // 1) 쿠폰 사용 처리 (예외 발생 시 전체 롤백)
        userCoupons.forEach(UserCoupon::use);

        // 2) 잔액 차감 처리 (예외 발생 시 전체 롤백)
        balance.deduct(order.getTotalPrice());

        // 3) 주문 상태 변경 및 이벤트 등록
        order.markAsPaid();
    }
}
