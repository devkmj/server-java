package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryJpaImpl implements OrderRepository {
    @Override
    public void save(Order order) {

    }
}
