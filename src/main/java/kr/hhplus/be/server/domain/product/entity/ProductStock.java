package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import lombok.Getter;

@Entity
@Getter
@Table(name = "product_stock", indexes = {
        @Index(name = "idx_product_stock_product_id", columnList = "product_id")
})
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

    public void validateEnough(int qty) {
        if (this.stock < qty) {
            throw new IllegalArgumentException("상품 재고가 부족합니다");
        }
    }

    public void decrease(int qty) {
        validateEnough(qty);
        this.stock -= qty;
        if(this.stock == 0) {
            product.markAsSoldOut();
        }
    }

    public boolean hasEnough(int qty) {
        return this.stock >= qty;
    }

}
