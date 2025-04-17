package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.application.user.UserCouponService;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user_coupon")
public class UserCouponController {

    private final UserCouponService userCouponService;

    public UserCouponController(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }


}
