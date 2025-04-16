package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItemRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepositoryJpaImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderIteJpamRepository;

    public OrderItemRepositoryJpaImpl(OrderItemJpaRepository orderIteJpamRepository) {
        this.orderIteJpamRepository = orderIteJpamRepository;
    }
}
