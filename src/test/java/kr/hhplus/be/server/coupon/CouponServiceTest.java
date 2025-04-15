package kr.hhplus.be.server.coupon;

import kr.hhplus.be.server.application.coupon.CouponService;
import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import org.apache.juli.logging.Log;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 발급 성공")
    void 쿠폰_발급_성공(){
        // given
        Long userId = 1L;
        Long couponId = 10L;

        Coupon coupon = new Coupon(30, 100, 60,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusMonths(1));

        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(couponRepository.existsByUserIdAndCouponId(userId, couponId)).willReturn(false);

        // when & then
        assertThatCode(() -> couponService.issueCoupon(userId, couponId))
                .doesNotThrowAnyException();

        verify(userCouponRepository).save(Mockito.any(UserCoupon.class));
    }

    @Test
    @DisplayName("사용자 인증 실패")
    void 사용자_인증_실패(){
        Long userId = 1L;
        Long couponId = 10L;

        Coupon coupon = new Coupon(30, 100, 60,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusMonths(1));

        given(userService.findByUserId(userId)).willThrow(new IllegalArgumentException("존재하지 않는 사용자입니다"));

        assertThatThrownBy(()-> couponService.issueCoupon(userId, couponId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    @DisplayName("쿠폰 조회 실패")
    void 쿠폰_조회_실패(){

    }

    @Test
    @DisplayName("이미 발급된 쿠폰")
    void 이미_발급된_쿠폰(){

    }

    @Test
    @DisplayName("발급 가능 수량을 초과했을 때 예외 발생")
    void 재고_부족(){
        Long userId = 1L;
        Long couponId = 10L;

        User user = new User("TEST");
        Coupon coupon = new Coupon(30, 100, 100,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusMonths(1));

        System.out.println(coupon.getIssuedCount());
        System.out.println(coupon.getTotalCount());


        given(userService.findByUserId(userId)).willReturn(user);
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(coupon.issue(user)).willThrow(new IllegalStateException("발급 가능 수량을 초과했습니다"));

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(userId, couponId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("발급 가능 수량을 초과했습니다");
    }
}
