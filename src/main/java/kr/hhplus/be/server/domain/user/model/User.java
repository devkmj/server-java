package kr.hhplus.be.server.domain.user.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;

import java.util.List;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_name", columnList = "name")
})
public class User extends BaseTimeEntity {

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
