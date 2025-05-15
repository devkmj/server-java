package kr.hhplus.be.server.interfaces.api.coupon.response;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponResponse {

    private Long id;
    private String name;
    private long totalCount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;

    public static CouponResponse from(Coupon coupon) {
        CouponResponse resp = new CouponResponse();
        resp.setId(coupon.getId());
        resp.setTotalCount(coupon.getTotalCount());
        resp.setValidFrom(coupon.getValidFrom());
        resp.setValidUntil(coupon.getValidUntil());
        return resp;
    }
}
