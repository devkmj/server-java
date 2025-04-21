package kr.hhplus.be.server.domain.balance.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.order.exception.InsufficientBalanceException;

@Entity
@Table(name = "balance", indexes = {
        @Index(name = "idx_balance_user_id", columnList = "userId"),
        @Index(name = "idx_balance_user_id_balance", columnList = "userId, balance")
})
public class Balance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer balance;

    protected Balance() {}

    public Balance(Long userId, Integer balance) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
        }
        this.userId = userId;
        this.balance = balance;
    }

    /**
     * 잔액 검증
     */
    public void validateSufficient(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (this.balance < amount) {
            throw new InsufficientBalanceException("잔액이 부족합니다.");
        }
    }

    /**
     * 잔액 차감
     */
    public void deduct(int amount) {
        validateSufficient(amount);
        this.balance -= amount;
    }

    /**
     * 잔액 충전
     */
    public void charge(int amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다");
        }

        this.balance += amount;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getBalance() {
        return balance;
    }
}
