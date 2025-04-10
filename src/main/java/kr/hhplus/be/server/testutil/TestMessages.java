package kr.hhplus.be.server.testutil;

public final class TestMessages {

    public static final String REQUIRED_NAME = "상품 이름은 필수입니다";
    public static final String INVALID_PRICE = "상품 가격은 0보다 커야 합니다";
    public static final String REQUIRED_STATUS = "상품 상태는 필수입니다";
    public static final String NOT_FOUND_PRODUCT = "상품을 찾을 수 없습니다.";

    private TestMessages() {
        // 생성자 private 처리 (생성 방지)
    }
}
