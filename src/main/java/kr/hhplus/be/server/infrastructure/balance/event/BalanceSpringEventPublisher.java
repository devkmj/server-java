package kr.hhplus.be.server.infrastructure.balance.event;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceDeductFailedEvent;
import kr.hhplus.be.server.domain.balance.event.BalanceEventPublisher;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
public class BalanceSpringEventPublisher implements BalanceEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public BalanceSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(BalanceDeductedEvent evt) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[onBalanceDeducted publish] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, evt.getClass().getSimpleName(), evt.getOrderId(), evt);
        applicationEventPublisher.publishEvent(evt);
    }

    @Override
    public void publish(BalanceDeductFailedEvent event) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[BalanceDeductFailedEvent publish] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, event.getClass().getSimpleName(), event.getOrderId(), event);
        applicationEventPublisher.publishEvent(event);
    }
}