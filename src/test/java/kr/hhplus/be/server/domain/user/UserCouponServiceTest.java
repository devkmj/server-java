package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.service.UserCouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @Test
    @DisplayName("사용자 쿠폰을 롤백한다")
    void it_calls_save_for_each_coupon() {
        UserCoupon c1 = mock(UserCoupon.class);
        UserCoupon c2 = mock(UserCoupon.class);

        userCouponService.rollbackUserCoupons(List.of(c1, c2));
        verify(userCouponRepository).save(c1);
        verify(userCouponRepository).save(c2);
    }
}
