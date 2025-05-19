package kr.hhplus.be.server.application.ranking.port;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.domain.order.entity.Order;
import java.util.EventListener;


public interface RankingUpdater {
    /**
     * 주어진 대상(order or productId)에 대해
     * 지정된 기간(period)과 이벤트 타입(rankingEventType)에 맞춰
     * 랭킹을 갱신한다.
     */
    void update(
            Order order,
            PeriodType period,
            RankingEventType rankingEventType
    );

    void update(
            Long productId,
            PeriodType period,
            RankingEventType rankingEventType
    );
}