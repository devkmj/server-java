package kr.hhplus.be.server.domain.ranking.batch;

import kr.hhplus.be.server.application.ranking.dto.RankingItem;
import kr.hhplus.be.server.domain.ranking.entity.RankingSnapshot;
import kr.hhplus.be.server.domain.ranking.repository.RankingSnapshotRepository;
import kr.hhplus.be.server.infrastructure.ranking.repository.WeeklyRankingRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class WeeklyRankingSnapshotJob {

    private final WeeklyRankingRedisRepository weeklyRepo;
    private final RankingSnapshotRepository snapshotRepo;

    /**
     * 매주 월요일 00:10에 지난 주(iso week)의 주간 랭킹을 스냅샷 적재
     */
    @Scheduled(cron = "0 10 0 * * MON")
    @Transactional
    public void snapshotWeekly() {
        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int currentWeek = today.get(wf.weekOfWeekBasedYear());
        int currentYear = today.get(wf.weekBasedYear());

        // 전주 key 계산
        int prevWeek = currentWeek - 1;
        if (prevWeek < 1) {
            prevWeek = 52;
            currentYear -= 1;
        }
        String periodKey = currentYear + String.format("-W%02d", prevWeek);
        LocalDate snapshotDate = today.minusWeeks(1);

        // (1) 기존 스냅샷 삭제
        snapshotRepo.deleteBySnapshotDateAndPeriodType(snapshotDate, "WEEKLY");

        // (2) Redis에서 Union 기반 Top-N 조회
        List<RankingItem> items = weeklyRepo.findTopNWeekly(100);

        // (3) DB에 배치 저장
        List<RankingSnapshot> snapshots = items.stream()
                .map(i -> new RankingSnapshot(snapshotDate, "WEEKLY", i.getProductId(), i.getScore()))
                .toList();
        snapshotRepo.saveAll(snapshots);
    }
}
