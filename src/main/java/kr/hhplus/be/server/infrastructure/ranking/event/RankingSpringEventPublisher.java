package kr.hhplus.be.server.infrastructure.ranking.event;

import kr.hhplus.be.server.domain.ranking.event.RankingEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Primary
public class RankingSpringEventPublisher implements RankingEventPublisher {
    private final RankingEventPublisher eventPublisher;

    @Override
    public void publish(Object event) {
        eventPublisher.publish(event);
    }
}
