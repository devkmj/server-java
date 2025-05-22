package kr.hhplus.be.server.application.ranking.port;

import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import java.util.List;

public interface RankingQuery {
    List<RankingItem> getDailyTop(int limit);
    List<RankingItem> getWeeklyTop(int limit);
    List<RankingItem> getRealtimeTop(int limit);
}
