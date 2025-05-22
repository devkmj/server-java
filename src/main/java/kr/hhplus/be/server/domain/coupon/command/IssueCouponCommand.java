package kr.hhplus.be.server.domain.coupon.command;

import kr.hhplus.be.server.interfaces.api.coupon.request.IssueCouponRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class IssueCouponCommand {

    private Long userId;
    private Long couponId;

    public IssueCouponCommand(Long userId, Long couponId) {
        if(userId == null || userId < 0) {
            throw new IllegalArgumentException("유효한 사용자 ID가 아닙니다");
        }
        if(couponId == null || couponId < 0) {
            throw new IllegalArgumentException("유효한 쿠폰 ID가 아닙니다");
        }
        this.userId = userId;
        this.couponId = couponId;
    }

    public static IssueCouponCommand from(IssueCouponRequest request) {
        IssueCouponCommand command = new IssueCouponCommand();
        command.userId = request.getUserId();
        command.couponId = request.getCouponId();
        return command;
    }
}
