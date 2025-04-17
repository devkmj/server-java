package kr.hhplus.be.server.application.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserCoupon;
import kr.hhplus.be.server.domain.user.UserCouponRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Rollback
@Transactional
@DisplayName("Coupon 통합 테스트")
public class CouponIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private CouponRepository couponRepository;
    @Autowired private UserCouponRepository userCouponRepository;

    private User user;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("선착순 유저"));
        coupon = couponRepository.save(new Coupon(10, 100, 1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)));
    }

    @BeforeEach
    void clear() {
        userCouponRepository.deleteAll();
        couponRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 발급 성공 - 선착순 1명")
    void 쿠폰_발급_성공() throws Exception {
        IssueCouponCommand request = new IssueCouponCommand(user.getId(), coupon.getId());

        mockMvc.perform(post("/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("쿠폰 발급 성공"));
    }

    @Test
    @DisplayName("동일한 쿠폰 중복 발급 시 예외 발생")
    void 중복_발급_예외() throws Exception {
        // 선발급
        userCouponRepository.save(new UserCoupon(user.getId(), coupon));

        IssueCouponCommand request = new IssueCouponCommand(user.getId(), coupon.getId());

        mockMvc.perform(post("/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 발급 받은 쿠폰"))
                .andExpect(jsonPath("$.data").value("이미 발급 받은 쿠폰입니다"));
    }

    @Test
    @DisplayName("선착순 수량 초과 시 예외 발생")
    void 선착순_수량_초과() throws Exception {
        coupon = couponRepository.save(new Coupon(10, 1, 1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)));

        IssueCouponCommand request = new IssueCouponCommand(user.getId(), coupon.getId());

        mockMvc.perform(post("/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("발급 가능 수량을 초과했습니다"));
    }

    @Test
    @DisplayName("만료된 쿠폰 발급 요청 시 예외 발생")
    void 만료된_쿠폰_예외() throws Exception {
        Coupon expired = couponRepository.save(new Coupon(10, 100, 5,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(1)));

        IssueCouponCommand request = new IssueCouponCommand(user.getId(), expired.getId());

        mockMvc.perform(post("/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data").value("유효하지 않은 쿠폰입니다"));
    }
}
