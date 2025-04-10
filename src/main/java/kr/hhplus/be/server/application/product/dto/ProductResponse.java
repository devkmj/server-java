package kr.hhplus.be.server.application.product.dto;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStatus;

public record ProductResponse(
        Long id,
        String name,
        int price,
        ProductStatus status
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStatus()
        );
    }

}