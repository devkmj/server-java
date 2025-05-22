package kr.hhplus.be.server.domain.product.validator;

import kr.hhplus.be.server.domain.product.entity.ProductStatus;

public class ProductValidator {

    public static void validate(String name, int price, ProductStatus status) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품 이름은 필수입니다");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다");
        }
        if (status == null) {
            throw new IllegalArgumentException("상품 상태는 필수입니다");
        }
    }
}
