package kr.hhplus.be.server.domain.ranking.batch;

import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.domain.ranking.entity.RankingSnapshot;
import kr.hhplus.be.server.domain.ranking.repository.RankingSnapshotRepository;
import kr.hhplus.be.server.infrastructure.ranking.repository.DailyRankingRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyRankingSnapshotJob {

    private final DailyRankingRedisRepository dailyRepo;
    private final RankingSnapshotRepository snapshotRepo;

    /**
     * 매일 00:05에 전일( yesterday )의 일간 랭킹을 스냅샷으로 DB에 적재
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void snapshotDaily() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // (1) 기존 스냅샷 삭제
        snapshotRepo.deleteBySnapshotDateAndPeriodType(yesterday, "DAILY");

        // (2) Redis에서 Top-N 뽑아오기 (원하는 limit 갯수만큼)
        List<RankingItem> items = dailyRepo.findTopNDaily(30);

        // (3) DB에 배치 저장
        List<RankingSnapshot> snapshots = items.stream()
                .map(i -> new RankingSnapshot(yesterday, "DAILY", i.getProductId(), i.getScore()))
                .toList();
        snapshotRepo.saveAll(snapshots);
    }

}
