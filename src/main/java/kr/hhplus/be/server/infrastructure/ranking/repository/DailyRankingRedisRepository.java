package kr.hhplus.be.server.infrastructure.ranking.repository;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.application.ranking.port.DailyRankingProvider;
import kr.hhplus.be.server.application.ranking.port.RankingProvider;
import kr.hhplus.be.server.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DailyRankingRedisRepository implements RankingProvider, DailyRankingProvider {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String KEY_PREFIX = "popular:daily:";
    private final StringRedisTemplate redis;

    @Override
    public PeriodType getPeriod() {
        return PeriodType.DAILY;
    }

    /**
     * 오늘자 일간 랭킹 ZSET에서 Top-N 아이템을 조회합니다.
     */
    @Override
    public List<RankingItem> getTop(int limit) {
        String key = KEY_PREFIX + LocalDate.now().format(DATE_FMT);
        BoundZSetOperations<String, String> ops = redis.boundZSetOps(key);

        Set<ZSetOperations.TypedTuple<String>> tuples =
                ops.reverseRangeWithScores(0, limit - 1);

        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }
        return tuples.stream()
                .map(t -> new RankingItem(
                        Long.valueOf(t.getValue()),
                        t.getScore()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 주문(order)의 각 아이템 수량만큼
     * daily 랭킹 ZSET에 점수를 증가시킵니다.
     */
    @Override
    public void increment(Order order) {
        // 키 생성: popular:daily:20250514 형태
        String key = KEY_PREFIX + LocalDate.now().format(DATE_FMT);

        // ZSET bound ops 얻기
        BoundZSetOperations<String, String> ops = redis.boundZSetOps(key);

        // 각 OrderItem 당 qty만큼 score 증가
        order.getOrderItems().forEach(item ->
                ops.incrementScore(
                        String.valueOf(item.getProductId()),
                        item.getQty()
                )
        );

        // TTL 설정 8일
        ops.getOperations().expire(key, Duration.ofDays(8));
    }

}
