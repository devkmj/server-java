package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "product_sales_summary", indexes = {
        @Index(name ="idx_product_sales_summary_product_id", columnList = "productId"),
        @Index(name ="idx_product_sales_summary_total_qty", columnList = "totalQty"),
        @Index(name ="idx_product_sales_summary_last_sold_at", columnList = "lastSoldAt"),
})
public class ProductSalesSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long totalQty;

    private LocalDate orderedAt;

    protected ProductSalesSummary() {}

    public ProductSalesSummary(Long productId, Long totalQty, LocalDate orderedAt) {
        this.productId = productId;
        this.totalQty = totalQty;
        this.orderedAt = orderedAt;
    }

    public void increaseQty(long qty) {
        this.totalQty += qty;
    }
}
