package kr.hhplus.be.server.application.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.model.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Rollback
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

    @Test
    @DisplayName("동시 쿠폰 발급 요청 시 하나만 성공해야 한다")
    void 동시_쿠폰_발급_테스트() throws InterruptedException {
        // given
        Long userId = user.getId();
        Coupon Limitedcoupon = couponRepository.save(new Coupon(10, 100, 99,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)));

        IssueCouponCommand command = new IssueCouponCommand(
                userId,
                Limitedcoupon.getId()
        );

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(post("/coupons/issue")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(command)))
                            .andExpect(result -> {
                                if (result.getResponse().getStatus() == 200) {
                                    successCount.incrementAndGet();
                                } else {
                                    System.out.println(result.getResponse().getContentAsString());
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        System.out.println("성공한 쿠폰 발급 수: " + successCount.get());
        assertThat(successCount.get()).isEqualTo(1);
    }
}
