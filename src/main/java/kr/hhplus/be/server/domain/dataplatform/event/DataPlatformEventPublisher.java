package kr.hhplus.be.server.domain.dataplatform.event;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;

public interface DataPlatformEventPublisher {
    void publish(OrderConfirmedEvent event);
}
