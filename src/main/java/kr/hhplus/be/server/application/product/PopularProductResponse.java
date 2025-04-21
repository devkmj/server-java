package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.model.Product;

public record PopularProductResponse(
        Long productId,
        String name,
        int price,
        Long totalSold
) {
    public static PopularProductResponse from(Product product, Long totalSold) {
        return new PopularProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                totalSold
        );
    }
}