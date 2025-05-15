package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponRedisInitializer {

    private final CouponService couponService;
    private final RedissonClient redissonClient;
 
    @EventListener(ContextRefreshedEvent.class)
    public void initAllActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        for (Coupon coupon : couponService.findAllActiveCoupons()) {
            initCoupon(coupon, now);
        }
    }

    public void initCoupon(Coupon coupon) {
        initCoupon(coupon, LocalDateTime.now());
    }

    private void initCoupon(Coupon coupon, LocalDateTime now) {
        long seconds = Duration.between(now, coupon.getValidUntil()).getSeconds();
        if (seconds <= 0) {
            return;  // 이미 만료된 쿠폰은 건너뛰기
        }

        String id = String.valueOf(coupon.getId());

        // Counter 초기화
        RAtomicLong counter = redissonClient.getAtomicLong("coupon:" + id + ":count");
        counter.set(coupon.getTotalCount());
        counter.expire(seconds, TimeUnit.SECONDS);

        // Issued-users Set 초기화
        RSet<String> issuedUsers = redissonClient.getSet("coupon:" + id + ":users");
        issuedUsers.clear();
        issuedUsers.expire(seconds, TimeUnit.SECONDS);
    }
}
