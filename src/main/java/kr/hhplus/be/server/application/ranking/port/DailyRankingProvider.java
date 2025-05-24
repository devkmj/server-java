package kr.hhplus.be.server.application.ranking.port;

import kr.hhplus.be.server.domain.order.entity.Order;

public interface DailyRankingProvider {
    void increment(Order order);
}
