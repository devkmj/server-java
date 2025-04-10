package kr.hhplus.be.server.application.order.dto;

public class OrderItemCommand {

    private Long orderId;
    private Long productId;
    private int qty;
    private int price;

    public OrderItemCommand(Long orderId, Long productId, int qty, int price) {
        this.orderId = orderId;
        this.productId = productId;
        this.qty = qty;
        this.price = price;
    }

    public OrderItemCommand(Long productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
