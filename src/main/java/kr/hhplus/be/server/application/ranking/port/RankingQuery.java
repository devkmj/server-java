package kr.hhplus.be.server.application.ranking.port;

import kr.hhplus.be.server.application.ranking.dto.PeriodType;
import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import java.util.List;

public interface RankingQuery {
    List<RankingItem> getTop(PeriodType period, int limit);
}
