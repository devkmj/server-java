package kr.hhplus.be.server.infrastructure.ranking.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import kr.hhplus.be.server.domain.ranking.event.RankingEventPublisher;

@Component
@Primary
public class RankingSpringEventPublisher implements RankingEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public RankingSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(Object event) {
        applicationEventPublisher.publishEvent(event);
    }
}
