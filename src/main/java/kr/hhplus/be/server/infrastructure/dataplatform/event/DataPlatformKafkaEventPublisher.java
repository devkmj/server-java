package kr.hhplus.be.server.infrastructure.dataplatform.event;

import kr.hhplus.be.server.domain.dataplatform.event.DataPlatformEventPublisher;
import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformKafkaEventPublisher implements DataPlatformEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "data_platform_order_events";

    @Override
    public void publish(OrderConfirmedEvent event) {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }

        log.info("[EVENT_PUBLISH] traceId={}, eventType={}, aggregateId={}, payload={}",
                traceId, event.getClass().getSimpleName(), event.getOrderId(), event);

        Message<OrderConfirmedEvent> message = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .setHeader("eventType", event.getClass().getSimpleName())
                .setHeader("traceId", traceId)
                .setHeader(KafkaHeaders.KEY, event.getOrderId().toString())
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent {} for order {}", event.getClass().getSimpleName(), event.getOrderId());
                    } else {
                        log.error("Failed to send {} for order {}", event.getClass().getSimpleName(), event.getOrderId(), ex);
                    }
                });
    }
}
