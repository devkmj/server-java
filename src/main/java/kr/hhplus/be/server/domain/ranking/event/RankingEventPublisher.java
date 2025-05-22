package kr.hhplus.be.server.domain.ranking.event;

public interface RankingEventPublisher {
    void publish(Object event);
}
