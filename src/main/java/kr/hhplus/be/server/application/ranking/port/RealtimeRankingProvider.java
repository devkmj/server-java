package kr.hhplus.be.server.application.ranking.port;

import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.domain.order.entity.Order;

public interface RealtimeRankingProvider {
    void increment(Long productId, RankingEventType type);
    void increment(Order order, RankingEventType rankingEventType);
}
