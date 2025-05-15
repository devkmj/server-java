package kr.hhplus.be.server.infrastructure.ranking.repository;

import kr.hhplus.be.server.domain.ranking.entity.RankingSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface RankingSnapshotJpaRepository extends JpaRepository<RankingSnapshot, Long> {
    void deleteBySnapshotDateAndPeriodType(LocalDate date, String daily);
}
