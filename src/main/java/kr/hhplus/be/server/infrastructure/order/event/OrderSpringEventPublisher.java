package kr.hhplus.be.server.infrastructure.order.event;

import kr.hhplus.be.server.domain.order.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        applicationEventPublisher.publishEvent(orderCreatedEvent);
    }

    @Override
    public void publish(OrderFailedEvent orderFailedEvent) {
        applicationEventPublisher.publishEvent(orderFailedEvent);
    }

    @Override
    public void publish(OrderConfirmedEvent orderConfirmedEvent) {
        applicationEventPublisher.publishEvent(orderConfirmedEvent);
    }
}
