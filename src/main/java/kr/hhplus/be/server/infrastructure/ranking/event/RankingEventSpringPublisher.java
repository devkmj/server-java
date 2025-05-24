package kr.hhplus.be.server.infrastructure.ranking.event;

import kr.hhplus.be.server.domain.ranking.event.RankingEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class RankingEventSpringPublisher implements RankingEventPublisher {
    private final RankingEventPublisher eventPublisher;

    public RankingEventSpringPublisher(RankingEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void publish(Object event) {
        eventPublisher.publish(event);
    }
}
