package kr.hhplus.be.server.interfaces.api.ranking.event;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingEventType;
import kr.hhplus.be.server.application.ranking.port.RankingUpdatePort;
import kr.hhplus.be.server.domain.product.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingProductEventListener {

    private final RankingUpdatePort updater;

    @EventListener
    public void onProductViewed(ProductViewedEvent evt) {
        updater.update(evt.getProductId(), PeriodType.REALTIME, RankingEventType.view);
    }
}
