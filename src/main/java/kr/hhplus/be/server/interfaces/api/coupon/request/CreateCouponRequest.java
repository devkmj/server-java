package kr.hhplus.be.server.interfaces.api.coupon.request;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCouponRequest {

    private int totalCount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private int rate;

    public Coupon toEntity() {
        return Coupon.builder()
                .totalCount(totalCount)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .rate(rate)
                .build();
    }
}
