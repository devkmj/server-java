package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item", indexes = {
        @Index(name = "idx_order_item_order_id" , columnList = "orderId"),
        @Index(name = "idx_order_item_product_id" ,columnList = "productId"),
        @Index(name = "idx_order_item_price" ,columnList = "price")
})
public class OrderItem extends BaseTimeEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 주문
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Getter
    private int qty;

    @Getter
    private int price; // 스냅샷용 상품 가격

    protected OrderItem() {}

    public OrderItem(Product product, int qty, int price) {
        this.product = product;
        this.qty = qty;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static OrderItem of(Product product, int qty){
        if(qty <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다");
        }
        return new OrderItem(product, qty, product.getPrice());
    }

    public int getTotalPrice() {
        return qty * price;
    }

    public Long getProductId() {
        return product.getId();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}