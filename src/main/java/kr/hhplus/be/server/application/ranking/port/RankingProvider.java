package kr.hhplus.be.server.application.ranking.port;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingItem;

import java.util.List;

public interface RankingProvider {
    PeriodType getPeriod();
    List<RankingItem> getTop(int limit);
}
