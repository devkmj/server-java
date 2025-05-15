package kr.hhplus.be.server.application.ranking.service;

import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.application.ranking.port.RankingQuery;
import kr.hhplus.be.server.infrastructure.ranking.repository.DailyRankingRedisRepository;
import kr.hhplus.be.server.infrastructure.ranking.repository.RealtimeRankingRedisRepository;
import kr.hhplus.be.server.infrastructure.ranking.repository.WeeklyRankingRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingQueryService implements RankingQuery {
    private final DailyRankingRedisRepository dailyRepo;
    private final WeeklyRankingRedisRepository weeklyRepo;
    private final RealtimeRankingRedisRepository realtimeRepo;

    @Cacheable(cacheNames = "dailyRanking", key = "#limit", sync = true)
    @Override
    public List<RankingItem> getDailyTop(int limit) {
        return dailyRepo.findTopNDaily(limit);
    }

    @Cacheable(cacheNames = "weeklyRanking", key = "#limit", sync = true)
    @Override
    public List<RankingItem> getWeeklyTop(int limit) {
        return weeklyRepo.findTopNWeekly(limit);  // Union 방식 구현
    }

    @Cacheable(value = "popularRealtimeTopN", key = "#limit", sync = true)
    @Override
    public List<RankingItem> getRealtimeTop(int limit) {
        return realtimeRepo.findTopN(limit);
    }
}