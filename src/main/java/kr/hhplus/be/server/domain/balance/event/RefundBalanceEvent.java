package kr.hhplus.be.server.domain.balance.event;

import kr.hhplus.be.server.domain.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundBalanceEvent {

    User user;
    int totalPrice;

    public RefundBalanceEvent(User user, int totalPrice) {
        this.user = user;
        this.totalPrice = totalPrice;
    }
}
