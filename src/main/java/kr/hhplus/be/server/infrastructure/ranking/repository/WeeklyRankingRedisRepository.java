package kr.hhplus.be.server.infrastructure.ranking.repository;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.application.ranking.port.DailyRankingProvider;
import kr.hhplus.be.server.application.ranking.port.RankingProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class WeeklyRankingRedisRepository implements RankingProvider{

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String DAILY_KEY_PREFIX = "popular:daily:";
    private static final String KEY_PREFIX = "popular:weekly:";
    private final StringRedisTemplate redis;

    @Override
    public PeriodType getPeriod() {
        return PeriodType.WEEKLY;
    }

    /**
     * ZUNIONSTORE를 이용해 지난 7일치 데일리 ZSET을 주간 ZSET(iso 주차 키)에 합산하고,
     * 상위 limit개를 조회합니다.
     */
    @Override
    public List<RankingItem> getTop(int limit) {
        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int week = today.get(wf.weekOfWeekBasedYear());
        int year = today.get(wf.weekBasedYear());
        String weeklyKey = KEY_PREFIX + year + String.format("-W%02d", week);

        // 1) 지난 7일치 daily 키 수집
        List<String> dailyKeys = IntStream.rangeClosed(1, 7)
                .mapToObj(i -> DAILY_KEY_PREFIX + today.minusDays(i).format(DATE_FMT))
                .collect(Collectors.toList());

        // 2) unionAndStore로 주간 키에 합산
        ZSetOperations<String, String> ops = redis.opsForZSet();
        // 마지막 7일치 데일리 키 리스트에서
        if (!dailyKeys.isEmpty()) {
            String baseKey = dailyKeys.get(0);
            Collection<String> otherKeys = dailyKeys.subList(1, dailyKeys.size());
            // union 저장
            ops.unionAndStore(baseKey, otherKeys, weeklyKey);
        }

        // TTL 설정
        redis.expire(weeklyKey, Duration.ofDays(8));  // 7일+버퍼

        // 3) 주간 키에서 Top-N 조회
        BoundZSetOperations<String, String> bound = redis.boundZSetOps(weeklyKey);
        Set<ZSetOperations.TypedTuple<String>> tuples = bound.reverseRangeWithScores(0, limit - 1);
        if (tuples == null || tuples.isEmpty()) {
            return List.of();
        }
        return tuples.stream()
                .map(t -> new RankingItem(Long.valueOf(t.getValue()), t.getScore()))
                .collect(Collectors.toList());
    }
}
