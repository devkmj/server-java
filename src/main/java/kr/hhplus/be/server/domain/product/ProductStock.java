package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.BaseTimeEntity;

@Entity
@Table(name = "product_stock")
public class ProductStock extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int stock;

    protected ProductStock() {}

    public ProductStock(Product product, int stock) {
        this.product = product;
        this.stock = stock;
    }

    public int getStock() {
        return stock;
    }
    
}
