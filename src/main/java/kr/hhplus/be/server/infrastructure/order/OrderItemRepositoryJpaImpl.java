package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OrderItemRepositoryJpaImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    public OrderItemRepositoryJpaImpl(OrderItemJpaRepository orderItemJpaRepository) {
        this.orderItemJpaRepository = orderItemJpaRepository;
    }
}
