package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import kr.hhplus.be.server.domain.product.validator.ProductValidator;

@DisplayName("ProductValidator 유효성 검사 테스트")
public class ProductValidatorTest {

    @Test
    @DisplayName("상품 이름이 null이거나 공백이면 예외 발생")
    void 상품_이름이_null_또는_공백이면_예외() {
        assertThatThrownBy(() -> ProductValidator.validate(null, 10000, ProductStatus.ON_SALE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름은 필수입니다");

        assertThatThrownBy(() -> ProductValidator.validate("  ", 10000, ProductStatus.ON_SALE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 이름은 필수입니다");
    }

    @Test
    @DisplayName("상품 가격이 0 이하이면 예외 발생")
    void 상품_가격이_0_또는_음수이면_예외() {
        assertThatThrownBy(() -> ProductValidator.validate("테스트상품", 0, ProductStatus.ON_SALE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 가격은 0보다 커야 합니다");
    }

    @Test
    @DisplayName("상품 상태가 null이면 예외 발생")
    void 상품_상태가_null이면_예외() {
        assertThatThrownBy(() -> ProductValidator.validate("테스트상품", 10000, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상품 상태는 필수입니다");
    }
}
