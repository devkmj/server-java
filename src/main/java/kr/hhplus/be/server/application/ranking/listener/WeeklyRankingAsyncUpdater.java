package kr.hhplus.be.server.application.ranking.listener;

import kr.hhplus.be.server.application.ranking.port.WeeklyRankingRebuilder;
import kr.hhplus.be.server.domain.ranking.event.RankingUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyRankingAsyncUpdater {

    private final WeeklyRankingRebuilder weeklyRankingRebuilder;

    @Async
    @EventListener(condition = "#event.period == PeriodType.DAILY")
    public void rebuildWeekly(RankingUpdatedEvent event) {
        weeklyRankingRebuilder.rebuildThisWeek();
    }
    
}
