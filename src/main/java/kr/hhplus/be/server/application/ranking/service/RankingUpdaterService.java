package kr.hhplus.be.server.application.ranking.service;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
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

    /**
     * @param order            주문 객체(상품 목록 포함) - 랭킹 갱신 대상
     * @param period           랭킹 갱신 기간 타입(DAILY, WEEKLY, REALTIME 등)
     * @param rankingEventType 랭킹 갱신 이벤트 타입(order, view, paid 등)
     */
    @Override
    public void update(Order order, PeriodType period, RankingEventType rankingEventType) {
        switch (period) {
            case DAILY:
                updateDaily(order);
                break;
            case REALTIME:
                updateRealtime(order, rankingEventType);
                break;
            default:
                throw new IllegalArgumentException("Unsupported period: " + period);
        }
    }

    /**
     * @param productId        랭킹 갱신 대상 상품 ID
     * @param period           랭킹 갱신 기간 타입(DAILY, WEEKLY, REALTIME 등)
     * @param rankingEventType 랭킹 갱신 이벤트 타입(order, view, paid 등)
     */
    @Override
    public void update(Long productId, PeriodType period, RankingEventType rankingEventType) {
        switch (period) {
            case REALTIME:
                updateRealtime(productId, rankingEventType);
                break;
            default:
                throw new IllegalArgumentException("Unsupported period: " + period);
        }
    }

    private void updateDaily(Order order) {
        dailyRepo.incrementDaily(order);
    }

    private void updateRealtime(Long productId, RankingEventType type) {
        realtimeRepo.increment(type, productId, 1);
    }

    private void updateRealtime(Order order, RankingEventType type) {
        order.getOrderItems().forEach(item -> {
            realtimeRepo.increment(type, item.getProductId(), item.getQty());
        });
    }


}
