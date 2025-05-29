package kr.hhplus.be.server.domain.order.event;

public interface OrderEventPublisher {
    void publish(OrderCreatedEvent orderCreatedEvent);
    void publish(OrderFailedEvent orderFailedEvent);
    void publish(OrderConfirmedEvent orderConfirmedEvent);
}
