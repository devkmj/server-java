package kr.hhplus.be.server.infrastructure.order.event;

import kr.hhplus.be.server.domain.order.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {
    private final OrderEventPublisher eventPublisher;

    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        eventPublisher.publish(orderCreatedEvent);
    }

    @Override
    public void publish(OrderFailedEvent orderFailedEvent) {
        eventPublisher.publish(orderFailedEvent);
    }

    @Override
    public void publish(OrderConfirmedEvent orderConfirmedEvent) {
        eventPublisher.publish(orderConfirmedEvent);
    }

    @Override
    public void publish(OrderInventoryFailedEvent orderInventoryFailedEvent) {
        eventPublisher.publish(orderInventoryFailedEvent);
    }
}
