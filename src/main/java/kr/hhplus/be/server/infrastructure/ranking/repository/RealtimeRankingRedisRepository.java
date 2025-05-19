package kr.hhplus.be.server.infrastructure.ranking.repository;

import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.config.redis.RealtimeRankingProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RealtimeRankingRedisRepository {
    private final StringRedisTemplate redis;
    private final RealtimeRankingProperties props;
    private static final String KEY_PREFIX = "popular:realtime:";
    private static final int minutesAgo = 10;

    public RealtimeRankingRedisRepository(StringRedisTemplate redis, RealtimeRankingProperties props) {
        this.redis = redis;
        this.props = props;
    }

    private String getCurrent10minKey(String eventType) {
        LocalDateTime now = LocalDateTime.now().minusMinutes(minutesAgo);
        int minute = now.getMinute() / 10 * 10; // 10분 단위 라운딩
        LocalDateTime rounded = now.withMinute(minute).withSecond(0).withNano(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        return KEY_PREFIX + eventType + ":" + rounded.format(fmt);
    }

    /**
     * 이벤트 발생 시 누적
     * 키 만료 30분 설정
     */
    public void increment(RankingEventType eventType, Long productId, int qty) {
        String key = getCurrent10minKey(eventType.name().toLowerCase());
        redis.opsForZSet().incrementScore(key, productId.toString(), qty);
        redis.expire(key, Duration.ofMinutes(30));
    }

    private List<String> getRecent10minKeys(String eventType) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        // 0, 10 분 전
        return List.of(0, 10).stream()
                .map(minsAgo -> {
                    LocalDateTime t = now.minusMinutes(minsAgo);
                    int min = t.getMinute() / 10 * 10;
                    LocalDateTime rounded = t.withMinute(min).withSecond(0).withNano(0);
                    return KEY_PREFIX + eventType + ":" + rounded.format(fmt);
                })
                .collect(Collectors.toList());
    }

    public List<RankingItem> findTopN(int limit) {
        var weights = props.getWeights(); // {view: 0.5, order: 1.0, paid: 3.0 ...}
        Map<Long, Double> scoreMap = new HashMap<>();

        // 각 이벤트타입(view/order/paid/confirm ...)
        for (var entry : weights.entrySet()) {
            String eventType = entry.getKey();
            double weight = entry.getValue();

            // 최근 20분(2개) ZSET
            for (String zsetKey : getRecent10minKeys(eventType)) {
                Set<ZSetOperations.TypedTuple<String>> tuples =
                        redis.opsForZSet().reverseRangeWithScores(zsetKey, 0, -1);
                if (tuples == null) continue;

                for (var tuple : tuples) {
                    String val = tuple.getValue();
                    if (val == null) continue;
                    Long productId;
                    try {
                        productId = Long.valueOf(val);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    double prev = scoreMap.getOrDefault(productId, Double.valueOf(0.0));
                    scoreMap.put(productId, Double.valueOf(prev + tuple.getScore() * weight));
                }
            }
        }

        // 내림차순, 동점이면 productId 오름차순
        return scoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(limit)
                .map(e -> new RankingItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

}
