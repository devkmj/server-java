package kr.hhplus.be.server.interfaces.api.user;

import kr.hhplus.be.server.domain.user.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user_coupon")
public class UserCouponController {

    private final UserCouponService userCouponService;

}
