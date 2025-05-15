package kr.hhplus.be.server.domain.ranking.repository;

import kr.hhplus.be.server.domain.ranking.entity.RankingSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface RankingSnapshotRepository {
    void saveAll(List<RankingSnapshot> snapshots);
    void deleteBySnapshotDateAndPeriodType(LocalDate date, String daily);
}
