package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.order.command.OrderItemCommand;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStock;
import lombok.Getter;

@Getter
@Entity
@Table(name = "order_item", indexes = {
        @Index(name = "idx_order_item_order_id" , columnList = "orderId"),
        @Index(name = "idx_order_item_product_id" ,columnList = "productId"),
        @Index(name = "idx_order_item_price" ,columnList = "price")
})
public class OrderItem extends BaseTimeEntity<OrderItem> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 주문
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int qty;

    private int price; // 스냅샷용 상품 가격

    protected OrderItem() {}

    public OrderItem(Product product, int qty, int price) {
        this.product = product;
        this.qty = qty;
        this.price = price;
    }

    /**
     * OrderItemCommand 와 도메인 객체들을 넘겨받아,
     * 검증 후 새로운 OrderItem 을 만들어 반환하는 팩토리 메서드
     */
    public static OrderItem fromCommand(
            OrderItemCommand cmd,
            Product product,
            ProductStock stock
    ) {
        // 1) 수량 검증
        if (cmd.getQty() <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다");
        }
        // 2) 상품 상태 검증
        product.validateOrderable();
        // 3) 재고 검증
        stock.validateEnough(cmd.getQty());
        // 4) 실제 가격 스냅샷을 찍어 생성
        return new OrderItem(product, cmd.getQty(), product.getPrice());
    }

    public int getTotalPrice() {
        return qty * price;
    }

    public Long getProductId() {
        return product.getId();
    }

    public void assignTo(Order order) {
        this.order = order;
        if (!order.getOrderItems().contains(this)) {
            order.getOrderItems().add(this);
        }
    }

}