package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.entity.BaseTimeEntity;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "product_sales_summary", indexes = {
        @Index(name ="idx_product_sales_summary_product_id", columnList = "productId"),
        @Index(name ="idx_product_sales_summary_total_qty", columnList = "totalQty"),
        @Index(name ="idx_product_sales_summary_ordered_at", columnList = "orderedAt"),
})
public class ProductSalesSummary extends BaseTimeEntity<ProductSalesSummary> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="productId", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long totalQty;

    @Column(nullable = false)
    private LocalDate orderedAt;

    protected ProductSalesSummary() {}

    public ProductSalesSummary(Long productId, Long totalQty, LocalDate orderedAt) {
        this.productId = productId;
        this.totalQty = totalQty;
        this.orderedAt = orderedAt;
    }

    public void increaseQty(int qty) {
        this.totalQty += qty;
    }
}
