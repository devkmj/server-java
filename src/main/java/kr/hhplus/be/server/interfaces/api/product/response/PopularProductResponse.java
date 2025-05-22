package kr.hhplus.be.server.interfaces.api.product.response;

import kr.hhplus.be.server.domain.product.entity.Product;

public record PopularProductResponse(
        Long productId,
        String name,
        int price,
        Double score
) {
    public static PopularProductResponse from(Product product, Double score) {
        return new PopularProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                score
        );
    }
}