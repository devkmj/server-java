package kr.hhplus.be.server.interfaces.api.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderItemRequest{

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private int qty;
    private int price;

    public OrderItemRequest(Long productId, int qty, int price) {
        this.productId = productId;
        this.qty = qty;
        this.price = qty * price;
    }

}
