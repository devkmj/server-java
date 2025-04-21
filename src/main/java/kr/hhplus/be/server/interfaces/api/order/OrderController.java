package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderFacade orderFacade;

    public OrderController(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody OrderRequest request){
        OrderCommand command = OrderCommand.from(request);
        Order order  = orderFacade.order(command);
        OrderResponse response = OrderResponse.from(order);
        return ResponseEntity.ok(ApiResponse.success("주문 성공", response)); 
    }
}
