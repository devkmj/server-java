package kr.hhplus.be.server.domain.product.event;

import org.springframework.context.ApplicationEvent;

/**
 * 상품 상세 페이지가 조회될 때 발행되는 도메인 이벤트
 */
public class ProductViewedEvent extends ApplicationEvent {
    private final Long productId;

    public ProductViewedEvent(Object source, Long productId) {
        super(source);
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}