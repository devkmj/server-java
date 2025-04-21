package kr.hhplus.be.server.api.order;

public class OrderItemRequest {
    private Long productId;
    private int qty;
    private int price;

    public OrderItemRequest(Long productId, int qty, int price) {
        this.productId = productId;
        this.qty = qty;
        this.price = qty * price;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }
}
