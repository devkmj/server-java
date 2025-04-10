package kr.hhplus.be.server.domain.balance;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;

@Entity
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
        if(balance == null || balance <= 0) {
            throw new IllegalArgumentException("초기 잔액은 0 이상이어야 합니다.");
        }
        this.userId = userId;
        this.balance = balance;
    }

    public void increase(int amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("차감 금액은 0보다 커야 합니다.");
        }
        this.balance -= amount;
    }

    public void charge(int amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("차감 금액은 0보다 커야 합니다.");
        }
        if(this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
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

    public void validateEnough(int amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다");
        }
    }

    public void use(int totalPrice) {
        this.balance -= totalPrice;
    }
}
