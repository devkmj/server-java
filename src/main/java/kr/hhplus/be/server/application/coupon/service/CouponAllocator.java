package kr.hhplus.be.server.application.coupon.service;

import kr.hhplus.be.server.domain.coupon.command.IssueCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponAllocator {

    private final RedissonClient redissonClient;

    public String allocate(IssueCouponCommand command, Coupon coupon) {
        String couponKey = String.valueOf(command.getCouponId());
        String userKey   = String.valueOf(command.getUserId());

        // 중복 발급 방지
        RSet<String> issuedUsers = redissonClient.getSet("coupon:" + couponKey + ":users");
        if (!issuedUsers.add(userKey)) {
            throw new IllegalStateException("이미 발급된 쿠폰입니다.");
        }

        log.info("userKey = {}", userKey);
        log.info("issuedUsers.add(userKey) = {}", issuedUsers.add(userKey));
        log.info("issuedUsers size = {}", issuedUsers.size());

        // 재고 차감 (DECR)
        RAtomicLong stockCounter = redissonClient.getAtomicLong("coupon:" + couponKey + ":count");
        long remaining = stockCounter.decrementAndGet();
        if (remaining < 0) {
            stockCounter.incrementAndGet();
            issuedUsers.remove(userKey);
            throw new IllegalStateException("쿠폰 재고가 부족합니다.");
        }

        // 발급 티켓 UUID 생성
        String ticket = UUID.randomUUID().toString();
        return ticket;
    }
}
