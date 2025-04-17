package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderCommand;
import kr.hhplus.be.server.application.order.dto.OrderDto;
import kr.hhplus.be.server.application.order.dto.OrderMapper;
import kr.hhplus.be.server.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@RequestBody OrderRequest request){
        OrderCommand command = OrderCommand.from(request);
        OrderDto response = orderFacade.order(command);
        return ResponseEntity.ok(ApiResponse.success("주문 성공", response));
    }
}
