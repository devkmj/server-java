package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "product_sales_summary", indexes = {
        @Index(name ="idx_product_sales_summary_product_id", columnList = "productId"),
        @Index(name ="idx_product_sales_summary_total_qty", columnList = "totalQty"),
        @Index(name ="idx_product_sales_summary_last_sold_at", columnList = "lastSoldAt"),
})
public class ProductSalesSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "total_qty", nullable = false)
    private Long totalQty;

    @Column(name = "last_sold_at")
    private LocalDateTime lastSoldAt;

    protected ProductSalesSummary() {}

    public ProductSalesSummary(Long productId, Long totalQty, LocalDateTime lastSoldAt) {
        this.productId = productId;
        this.totalQty = totalQty;
        this.lastSoldAt = lastSoldAt;
    }

    // Getters
    public Long getProductId() {
        return productId;
    }

    public Long getTotalQty() {
        return totalQty;
    }

    public LocalDateTime getLastSoldAt() {
        return lastSoldAt;
    }

    // 업데이트 로직
    public void increaseQty(long qty) {
        this.totalQty += qty;
        this.lastSoldAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
}
