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
}
