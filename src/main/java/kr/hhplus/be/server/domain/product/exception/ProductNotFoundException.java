package kr.hhplus.be.server.domain.product.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("존재하지 않는 상품입니다.");
    }
}
