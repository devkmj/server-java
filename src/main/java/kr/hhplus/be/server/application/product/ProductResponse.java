package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.ProductStatus;

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