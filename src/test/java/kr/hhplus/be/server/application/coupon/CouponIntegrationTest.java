package kr.hhplus.be.server.application.coupon;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.interfaces.api.coupon.request.CreateCouponRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@DisplayName("쿠폰 통합 테스트")
public class CouponIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    UserRepository userRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        couponRepository.deleteAll();
        userCouponRepository.deleteAll();
    }

    @Test
    @DisplayName("쿠폰 생성 후 발급이 가능하다")
    public void 쿠폰_생성_후_발급_성공() throws Exception {
        User testUser = new User("testUser");
        userRepo.save(testUser);

        CreateCouponRequest createReq = new CreateCouponRequest();
        createReq.setTotalCount(1);
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);
        createReq.setRate(20);
        createReq.setTotalCount(100);
        createReq.setValidFrom(now.minusMinutes(1));
        createReq.setValidUntil(now.plusMinutes(10));

        String createJson = objectMapper.writeValueAsString(createReq);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andReturn();

        Coupon saved = couponRepository.findAll().get(0);

        String issueJson = "{\"couponId\":" + saved.getId() + ",\"userId\":"+testUser.getId()+"}";
        mockMvc.perform(post("/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(issueJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ticket").exists());
    }
}
