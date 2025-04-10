package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.Order;

public class OrderMapper {

    public static OrderDto toOrderDto(Order order) {
        if(order == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setOrderStatus(order.getStatus().name());
        dto.setCreatedDate(order.getCreateTime());
        return dto;
    }
}
