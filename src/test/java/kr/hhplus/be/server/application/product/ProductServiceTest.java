package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static kr.hhplus.be.server.testutil.TestMessages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("Product 도메인 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("존재하지 않는 상품 조회시 예외가 발생한다")
    void 존재하지_않는_상품_ID로_조회시_예외가_발생한다(){
        //given
        Long invalidProductId = 99L;
        given(productRepository.findById(invalidProductId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getById(invalidProductId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage(NOT_FOUND_PRODUCT);
    }

    @Test
    @DisplayName("상품 가격이 null 또는 0 이하인 경우 예외가 발생한다")
    void 상품_가격이_null_또는_음수인_경우_예외가_발생한다(){
        Long invalidProductId = 99L;
        //when & then
        assertThatThrownBy(() -> new Product("테스트 상품",0, ProductStatus.AVAILABLE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(INVALID_PRICE);

    }

    @Test
    @DisplayName("상품이 없는 경우 빈 리스트 반환")
    void 상품이_없는_경우_빈_리스트를_반환한다(){
        //given
        given(productRepository.findAll()).willReturn(Collections.emptyList());

        //when
        List<ProductResponse> result = productService.getAllProducts();

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("상품이 1개 이상일 때 정상 반환")
    void 상품이_2개_이상일_떄_정상적으로_반환된다() {
        //given
        List<Product> products = List.of(
                new Product("상품1", 1000, ProductStatus.ON_SALE),
                new Product("상품2", 2030000, ProductStatus.AVAILABLE)
        );
        given(productRepository.findAll()).willReturn(products);

        //when
        List<ProductResponse> result = productService.getAllProducts();

        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("전체 상품 목록 조회 - 정상 반환")
    void 전체_상품_목록_조회() {
        // given
        List<Product> products = List.of(
                new Product("상품1", 590000, ProductStatus.ON_SALE),
                new Product("상품2", 630000, ProductStatus.AVAILABLE),
                new Product("상품3", 982000, ProductStatus.ON_SALE)
        );
        given(productRepository.findAll()).willReturn(products);

        // when
        List<ProductResponse> result = productService.getAllProducts();

        // then
        assertThat(result.get(0)).isEqualTo(ProductResponse.from(products.get(0)));
        assertThat(result.get(1)).isEqualTo(ProductResponse.from(products.get(1)));
        assertThat(result.get(2)).isEqualTo(ProductResponse.from(products.get(2)));
    }

    @Test
    @DisplayName("상품 목록 정렬 순서 확인 (등록순)")
    void 정렬_순서_확인() {
        // given
        Product p1 = new Product("상품1", 120000, ProductStatus.ON_SALE);
        Product p2 = new Product("상품2", 930000, ProductStatus.ON_SALE);
        given(productRepository.findAll()).willReturn(List.of(p1, p2));

        // when
        List<ProductResponse> result = productService.getAllProducts();

        // then
        assertThat(result).containsExactly(
                ProductResponse.from(p1),
                ProductResponse.from(p2)
        );
    }

    @Test
    @DisplayName("상품 이름이 null이나 공백이면 예외 밸상")
    void 상품_이름이_null_또는_공백이면_예외() {
        assertThatThrownBy(() -> new Product(null, 0, ProductStatus.AVAILABLE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(REQUIRED_NAME);

        assertThatThrownBy(() -> new Product(" ", 1000, ProductStatus.ON_SALE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(REQUIRED_NAME);
    }

    @Test
    @DisplayName("상품 상태가 null이면 예외 발생")
    void 상품_상태가_null이면_예외() {
        assertThatThrownBy(() -> new Product("상품", 1000, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(REQUIRED_STATUS);
    }

    @Test
    @DisplayName("상품 ID로 조회시 정상적으로 반환된다")
    void 상품_ID로_정상조회() {
        // given
        Product product = new Product("상품", 5000, ProductStatus.ON_SALE);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        ProductResponse result = productService.getById(1L);

        // then
        assertThat(result).isEqualTo(ProductResponse.from(product));
    }

}
