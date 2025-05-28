package kr.hhplus.be.server.application.ranking.service;

import kr.hhplus.be.server.domain.ranking.event.RankingEventPublisher;
import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.application.ranking.port.DailyRankingProvider;
import kr.hhplus.be.server.application.ranking.port.RankingUpdatePort;
import kr.hhplus.be.server.application.ranking.port.RealtimeRankingProvider;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.ranking.event.RankingUpdatedEvent;
import lombok.RequiredArgsConstructor; 
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingUpdateService implements RankingUpdatePort {

    private final DailyRankingProvider dailyProvider;
    private final RealtimeRankingProvider realtimeProvider;
    private final RankingEventPublisher eventPublisher;

    /**
     * @param order            주문 객체(상품 목록 포함) - 랭킹 갱신 대상
     * @param period           랭킹 갱신 기간 타입(DAILY, WEEKLY, REALTIME 등)
     * @param rankingEventType 랭킹 갱신 이벤트 타입(order, view, paid 등)
     */
    @Override
    public void update(Order order, PeriodType period, RankingEventType rankingEventType) {
        switch (period) {
            case DAILY:
                dailyProvider.increment(order);
                eventPublisher.publish(new RankingUpdatedEvent(PeriodType.DAILY));
                break;
            case REALTIME:
                realtimeProvider.increment(order, rankingEventType);
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
                realtimeProvider.increment(productId, rankingEventType);
                break;
            default:
                throw new IllegalArgumentException("Unsupported period: " + period);
        }
    }

}
