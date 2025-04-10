package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;

@Entity
@Table(name = "products")
public class Product extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    protected Product() {}

    public Product(String name, int price, ProductStatus status) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다");
        }
        if (status == null) {
            throw new IllegalArgumentException("상품 상태는 필수입니다");
        }
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
}
