package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.Order;

import java.util.List;
import java.util.stream.Collectors;

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
        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(item -> new OrderItemDto(item.getId(), item.getProductId(), item.getQty(), item.getPrice()))
                .collect(Collectors.toList());

        dto.setItems(itemDtos);
        dto.setUsedCouponIds(order.getUserCoupon());
        return dto;
    }
}
