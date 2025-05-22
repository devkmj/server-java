package kr.hhplus.be.server.application.ranking.port;

import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.domain.order.entity.Order;
import java.util.EventListener;


public interface RankingUpdater {
    void updateRealtime(Order order, RankingEventType rankingEventType);
    void updateRealtime(Long productId, RankingEventType rankingEventType);
    void updateDaily(Order order);
}