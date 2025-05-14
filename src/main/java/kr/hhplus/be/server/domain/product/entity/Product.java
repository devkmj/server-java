package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import kr.hhplus.be.server.domain.product.validator.ProductValidator;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_status", columnList = "status"),
        @Index(name = "idx_product_created_at", columnList = "created_at"),
        @Index(name = "idx_status_created_at", columnList = "status, created_at")
})
public class Product extends BaseTimeEntity<Product> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    protected Product() {}

    public Product(String name, int price, ProductStatus status) {
        ProductValidator.validate(name, price, status);
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void markAsSoldOut() {
        this.status = ProductStatus.SOLD_OUT;
    }

    public void markAsAvailable() {
        this.status = ProductStatus.AVAILABLE;
    }

    public void markAsDiscontinued() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public void validateOrderable() {
        if (this.status != ProductStatus.AVAILABLE && this.status != ProductStatus.ON_SALE) {
            throw new IllegalArgumentException("판매중인 상품이 아닙니다.");
        }
    }

    public int calculateTotalPrice(int qty) {
        return this.price * qty;
    }
}
