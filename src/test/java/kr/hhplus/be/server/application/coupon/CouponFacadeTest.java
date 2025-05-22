package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserCouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class CouponFacadeTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @Test
    @DisplayName("쿼리 기반 중복 검사에서 race condition으로 중복 발급이 발생할 수 있다")
    void 중복발급_테스트() throws InterruptedException {
        Long couponId = 10L;
        Coupon coupon = new Coupon(30, 100, 50,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusMonths(1));
        Long userId = 1L;
        IssueCouponCommand command = new IssueCouponCommand(userId, couponId);

        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // Step 1: exists 쿼리 후 잠깐 대기
                    if (!userCouponRepository.existsByUserIdAndCouponId(command.getUserId(), command.getCouponId())) {
                        Thread.sleep(100); // 여기서 다른 스레드가 끼어들 여지를 줌

                        UserCoupon issued = new UserCoupon(command.getUserId(), coupon);
                        userCouponRepository.save(issued);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("에러 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 결과 확인
        List<UserCoupon> allIssued = userCouponRepository.findAll();
        System.out.println("발급된 쿠폰 수: " + allIssued.size());

        // 1건 초과 발급되었는지 체크
        assertThat(allIssued.size()).isGreaterThan(1); // 의도적으로 실패 유도
    }


}
