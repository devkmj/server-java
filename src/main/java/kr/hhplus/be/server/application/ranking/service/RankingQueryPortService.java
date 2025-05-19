package kr.hhplus.be.server.application.ranking.service;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.application.ranking.port.RankingProvider;
import kr.hhplus.be.server.application.ranking.port.RankingQueryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingQueryPortService implements RankingQueryPort {
    private final Map<PeriodType, RankingProvider> providerMap;

    public RankingQueryPortService(List<RankingProvider> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(RankingProvider::getPeriod, p -> p));
    }

    @Override
    public List<RankingItem> getTop(PeriodType period, int limit) {
        switch (period) {
            case REALTIME:
                return getRealtimeTop(limit);
            case DAILY:
                return getDailyTop(limit);
            case WEEKLY:
                return getWeeklyTop(limit);
            default:
                throw new IllegalArgumentException("Unsupported period: " + period);
        }
    }

    @Cacheable(cacheNames = "dailyRanking", key = "#limit", sync = true)
    public List<RankingItem> getDailyTop(int limit) {
        return providerMap.get(PeriodType.DAILY).getTop(limit);
    }

    @Cacheable(cacheNames = "weeklyRanking", key = "#limit", sync = true)
    public List<RankingItem> getWeeklyTop(int limit) {
        return providerMap.get(PeriodType.WEEKLY).getTop(limit);
    }

    @Cacheable(value = "popularRealtimeTopN", key = "#limit", sync = true)
    public List<RankingItem> getRealtimeTop(int limit) {
        return providerMap.get(PeriodType.REALTIME).getTop(limit);
    }
}