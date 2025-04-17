package kr.hhplus.be.server.application.product;

public record PopularProductResponse(
        Long productId,
        String name,
        int price,
        Long totalSold
) {}