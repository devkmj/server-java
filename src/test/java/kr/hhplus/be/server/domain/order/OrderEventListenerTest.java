package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.event.OrderConfirmedEvent;
import kr.hhplus.be.server.interfaces.api.dataplatform.event.DataPlatformOrderEventListener;
import kr.hhplus.be.server.domain.dataplatform.event.DataPlatformEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class OrderEventListenerTest {

    @Mock
    private DataPlatformEventPublisher dataPlatformPublisher;

    @InjectMocks
    private DataPlatformOrderEventListener listener;

    @Test
    @DisplayName("OrderConfirmedEvent 수신 시 데이터 플랫폼 퍼블리셔가 호출된다")
    void 주문_확정_이벤트_발행_후_데이터_플랫폼_퍼블리셔_호출() {
        // given
        long orderId = 456L;
        OrderConfirmedEvent evt = new OrderConfirmedEvent(orderId);

        // when
        listener.onOrderConfirmedEvent(evt);

        // then
        verify(dataPlatformPublisher).publish(argThat(pEvt ->
            pEvt.getOrderId().equals(orderId)
        ));
    }
}
