package kr.hhplus.be.server.infrastructure.ranking.repository;

import kr.hhplus.be.server.domain.ranking.entity.RankingSnapshot;
import kr.hhplus.be.server.domain.ranking.repository.RankingSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository; ;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RankingSnapshotRepositoryAdapter implements RankingSnapshotRepository {

    private final RankingSnapshotJpaRepository rankingSnapshotJpaRepository;

    @Override
    public void saveAll(List<RankingSnapshot> snapshots) {
        rankingSnapshotJpaRepository.saveAll(snapshots);
    }

    @Override
    public void deleteBySnapshotDateAndPeriodType(LocalDate date, String daily) {
        rankingSnapshotJpaRepository.deleteBySnapshotDateAndPeriodType(date, daily);
    }
}
