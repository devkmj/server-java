package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.UserCoupon;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_item")
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소속된 주문
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int qty;

    private int price; // 스냅샷용 상품 가격

    protected OrderItem() {}

    public OrderItem(Product product, int qty, int price) {
        this.product = product;
        this.qty = qty;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalPrice() {
        return qty * price;
    }

    public Product getProduct() {
        return product;
    }

    public Long getId(){
        return id;
    }

    public Long getProductId() {
        return product.getId();
    }

    public int getQty(){
        return qty;
    }

    public int getPrice() {
        return price;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}