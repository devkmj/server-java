package kr.hhplus.be.server.application.order.event;

import kr.hhplus.be.server.domain.balance.event.RefundBalanceEvent;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.user.event.RestoreUserCouponEvent;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CompensationEventHandler {

    private final BalanceService balanceService;
    private final UserCouponService userCouponService;

    @Async
    @EventListener
    public void handle(RefundBalanceEvent event) {
        balanceService.refund(event.getUser(), event.getTotalPrice());
    }

    @Async
    @EventListener
    public void handle(RestoreUserCouponEvent event) {
        userCouponService.restore(event.getUserCoupons());
    }
}
