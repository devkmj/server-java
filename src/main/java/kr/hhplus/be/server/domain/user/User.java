package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer balance;

    protected User() {}

    public User(String name, Integer balance) {
        this.name = name;
        this.balance = balance;
    }

    public void charge(Integer amount) {
        this.balance += amount;
    }

    public void deduct(Integer amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBalance() {
        return balance;
    }
}
