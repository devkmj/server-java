package kr.hhplus.be.server.infrastructure.order.event;

import kr.hhplus.be.server.domain.order.event.OrderCreatedEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventSpringPublisher implements OrderEventPublisher {
    private final OrderEventPublisher eventPublisher;

    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        eventPublisher.publish(orderCreatedEvent);
    }
}
