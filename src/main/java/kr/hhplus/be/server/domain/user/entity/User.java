package kr.hhplus.be.server.domain.user.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "users", indexes = {
        @Index(name = "idx_user_name", columnList = "name")
})
public class User extends BaseTimeEntity<User> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    protected User() {}

    public User(String name) {
        this.name = name;
    }

    public User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public boolean ownsCoupon(List<UserCoupon> coupons) {
        for (UserCoupon coupon : coupons) {
            if (!coupon.getUserId().equals(this.id)) {
                return false;
            }
        }
        return true;
    }
}
