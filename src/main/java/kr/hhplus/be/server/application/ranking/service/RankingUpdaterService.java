package kr.hhplus.be.server.application.ranking.service;

import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.application.ranking.port.RankingUpdater;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.infrastructure.ranking.repository.DailyRankingRedisRepository;
import kr.hhplus.be.server.infrastructure.ranking.repository.RealtimeRankingRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingUpdaterService implements RankingUpdater {

    private final DailyRankingRedisRepository dailyRepo;
    private final RealtimeRankingRedisRepository realtimeRepo;

    @Override
    public void updateDaily(Order order) {
        dailyRepo.incrementDaily(order);
    }

    @Override
    public void updateRealtime(Long productId, RankingEventType type) {
        realtimeRepo.increment(type, productId, 1);
    }

    @Override
    public void updateRealtime(Order order, RankingEventType type) {
        order.getOrderItems().forEach(item -> {
            realtimeRepo.increment(type, item.getProductId(), item.getQty());
        });
    }
}
