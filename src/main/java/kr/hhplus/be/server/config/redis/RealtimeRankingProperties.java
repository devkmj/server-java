package kr.hhplus.be.server.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "ranking.realtime")
public class RealtimeRankingProperties {
    /** eventType -> Redis ZSET key */
    private Map<String, String> eventKeys;

    /** eventType -> weight multiplier */
    private Map<String, Double> weights;

    /** ZUNIONSTORE 결과를 저장할 키 */
    private String combinedKey;

    /** 합산 ZSET TTL (일 단위) */
    private long combinedTtlDays;
}
