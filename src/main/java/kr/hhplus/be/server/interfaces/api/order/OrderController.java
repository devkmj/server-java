package kr.hhplus.be.server.interfaces.api.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.order.OrderPlacementService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.interfaces.api.order.request.OrderRequest;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.order.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderPlacementService placementService;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        Order order = placementService.placeOrder(OrderCommand.from(request));
        URI location = URI.create("/orders/" + order.getId());
        return ResponseEntity
                .created(location)
                .body(ApiResponse.success("주문 생성 완료", OrderResponse.from(order)));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ApiResponse.success("주문 상세 조회 성공", order);
    }
}
