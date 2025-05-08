package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.user.service.UserService;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
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
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
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

        // when & then
        assertThatCode(() -> couponService.issue(coupon, user))
                .doesNotThrowAnyException();

        verify(userCouponRepository).save(Mockito.any(UserCoupon.class));
    }

//    @Test
//    @DisplayName("사용자 인증 실패")
//    void 사용자_인증_실패(){
//        Long userId = 99L;
//        Long couponId = 10L;
//
//        Coupon coupon = new Coupon(30, 100, 60,
//                LocalDateTime.now().minusDays(4),
//                LocalDateTime.now().plusMonths(1));
//
//        given(userRepository.findById(userId)).willReturn(Optional.empty());
//
//        assertThatThrownBy(()-> couponService.issue(coupon, null))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("존재하지 않는 사용자입니다");
//    }
//
//    @Test
//    @DisplayName("유효하지 않은 쿠폰 조회 시 예외가 발생한다")
//    void 쿠폰_조회_실패(){
//        Long userId = 1L;
//        Long couponId = 999L;
//        User user = new User(userId, "Test");
//
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(couponRepository.findById(couponId)).willReturn(Optional.empty());
//
//        assertThatCode(() -> couponService.issue(null, user))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessageContaining("쿠폰을 찾을 수 없습니다");
//
//    }
//
//    @Test
//    @DisplayName("이미 발급된 쿠폰 조회 시 예외가 발생한다")
//    void 이미_발급된_쿠폰() {
//        // GIVEN
//        Long userId = 1L;
//        Long couponId = 999L;
//
//        User user = new User(userId, "Test");
//
//        // 테스트를 위한 수동 ID 설정 (coupon.getId() == 999L)
//        Coupon coupon = new Coupon(couponId, 30, 100, 50,
//                LocalDateTime.now().minusDays(4),
//                LocalDateTime.now().plusMonths(1));
//
//        // 이미 발급된 쿠폰 상태를 표현하는 유저-쿠폰 객체
//        UserCoupon userCoupon = new UserCoupon(userId, coupon);
//
//        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);
//
//        // Stubbing
//        given(userRepository.findById(userId)).willReturn(Optional.of(user));
//        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
//        given(userCouponRepository.existsByUserIdAndCouponId(userId, couponId)).willReturn(Boolean.valueOf(true));
//
//        // WHEN & THEN
//        assertThatThrownBy(() -> couponService.issue(coupon, user))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("이미 발급 받은 쿠폰입니다");
//    }

    @Test
    @DisplayName("발급 가능 수량을 초과했을 때 예외 발생한다")
    void 재고_부족(){
        Long userId = 1L;
        Long couponId = 10L;

        User user = new User("TEST");
        Coupon coupon = new Coupon(30, 100, 100,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusMonths(1));
        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);

        // when & then
        assertThatThrownBy(() -> couponService.issue(coupon, user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("발급 가능 수량을 초과했습니다");
    }

    @Test
    @DisplayName("쿠폰의 사용일이 쿠폰 사용기간에 해당하지 않을 경우 예외가 발생한다")
    void 유효하지_쿠폰(){
        Long userId = 1L;
        Long couponId = 10L;

        User user = new User("TEST");
        Coupon coupon = new Coupon(30, 100, 40,
                LocalDateTime.now().minusMonths(4),
                LocalDateTime.now().minusDays(1));
        IssueCouponCommand issueCouponCommand = new IssueCouponCommand(userId, couponId);

        // when & then
        assertThatThrownBy(() -> couponService.issue(coupon, user))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("유효하지 않은 쿠폰입니다");
    }

}
