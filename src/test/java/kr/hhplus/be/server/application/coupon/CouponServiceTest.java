package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.IssueCouponCommand;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Mock
    private UserRepository userRepository;

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
        User user = new User(userId, "Test");

        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));

        // when & then
        assertThatCode(() -> couponService.issueCoupon(issueCouponCommand))
                .doesNotThrowAnyException();

        verify(userCouponRepository).save(Mockito.any(UserCoupon.class));
    }

    @Test
    @DisplayName("사용자 인증 실패")
    void 사용자_인증_실패(){
        Long userId = 99L;
        Long couponId = 10L;

        Coupon coupon = new Coupon(30, 100, 60,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusMonths(1));

        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(()-> couponService.issueCoupon(issueCouponCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자입니다");
    }

    @Test
    @DisplayName("쿠폰 조회 실패")
    void 쿠폰_조회_실패(){
        Long userId = 1L;
        Long couponId = 999L;

        User user = new User(userId, "Test");
        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findById(couponId)).willReturn(Optional.empty());

        assertThatCode(() -> couponService.issueCoupon(issueCouponCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠폰을 찾을 수 없습니다");

    }

    @Test
    @DisplayName("이미 발급된 쿠폰")
    void 이미_발급된_쿠폰() {
        // GIVEN
        Long userId = 1L;
        Long couponId = 999L;

        User user = new User(userId, "Test");

        // 테스트를 위한 수동 ID 설정 (coupon.getId() == 999L)
        Coupon coupon = new Coupon(couponId, 30, 100, 100,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusMonths(1));

        // 이미 발급된 쿠폰 상태를 표현하는 유저-쿠폰 객체
        UserCoupon userCoupon = new UserCoupon(userId, coupon);

        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);

        // Stubbing
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).willReturn(Boolean.valueOf(true));

        // WHEN & THEN
        assertThatThrownBy(() -> couponService.issueCoupon(issueCouponCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 발급 받은 쿠폰입니다");
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
        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));

        // when & then
        assertThatThrownBy(() -> couponService.issueCoupon(issueCouponCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("발급 가능 수량을 초과했습니다");
    }
}
