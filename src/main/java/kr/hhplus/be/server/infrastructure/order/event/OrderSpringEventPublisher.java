package kr.hhplus.be.server.infrastructure.order.event;

import kr.hhplus.be.server.domain.order.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSpringEventPublisher implements OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[EVENT_PUBLISH] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, orderCreatedEvent.getClass().getSimpleName(), orderCreatedEvent.getOrderId(), orderCreatedEvent);
        applicationEventPublisher.publishEvent(orderCreatedEvent);

    }

    @Override
    public void publish(OrderFailedEvent orderFailedEvent) {
        log.info("[EVENT_PUBLISH OrderSpringEventPublisher] 수신된 이벤트: {}", orderFailedEvent);
        applicationEventPublisher.publishEvent(orderFailedEvent);
    }

    @Override
    public void publish(OrderConfirmedEvent orderConfirmedEvent) {
        log.info("[EVENT_PUBLISH OrderSpringEventPublisher] 수신된 이벤트: {}", orderConfirmedEvent);
        applicationEventPublisher.publishEvent(orderConfirmedEvent);
    }
}
