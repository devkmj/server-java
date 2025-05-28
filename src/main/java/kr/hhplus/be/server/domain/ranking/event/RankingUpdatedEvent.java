package kr.hhplus.be.server.domain.ranking.event;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RankingUpdatedEvent {
    PeriodType periodType;

    public RankingUpdatedEvent(PeriodType periodType) {
        this.periodType = periodType;
    }
}
