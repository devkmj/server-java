package kr.hhplus.be.server.domain.ranking.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "ranking_snapshot",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"snapshot_date", "period_type", "product_id"}))
public class RankingSnapshot {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="snapshot_date", nullable=false)
    private LocalDate snapshotDate;

    @Column(name="period_type", nullable=false, length=10)
    private String periodType;    // "DAILY" or "WEEKLY"

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(nullable=false)
    private Double score;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters, protected no-args ctor
    protected RankingSnapshot() {}

    public RankingSnapshot(LocalDate snapshotDate, String periodType, Long productId, Double score) {
        this.snapshotDate = snapshotDate;
        this.periodType  = periodType;
        this.productId   = productId;
        this.score       = score;
    }
}
