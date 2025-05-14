package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import kr.hhplus.be.server.domain.product.service.ProductService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        ProductService.class,
        ProductServiceCacheTest.TestConfig.class
})
class ProductServiceCacheTest {

    @Configuration
    @EnableCaching
    static class TestConfig {
        @Bean
        public CacheManager cacheManager() {
            // "top5Last3d" 캐시만 메모리 기반으로 생성
            return new ConcurrentMapCacheManager("top5Last3d");
        }

        @Bean
        public ProductRepository productRepository() {
            // ProductService 생성자용 더미 mock
            return mock(ProductRepository.class);
        }

        @Bean
        public ProductQueryRepository productQueryRepository() {
            // 캐시 대상 메서드 호출 시 사용할 mock
            return mock(ProductQueryRepository.class);
        }
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductQueryRepository productQueryRepository;

    private List<PopularProductResponse> fakeList;

    @BeforeEach
    void setUp() {
        // 테스트용 더미 데이터
        fakeList = List.of(
                new PopularProductResponse(1L, "A", 1000, 42L),
                new PopularProductResponse(2L, "B", 2000, 36L)
        );
        // 첫 호출에만 리포지토리가 실행되고, 그 뒤부터는 캐시에서 반환되도록 설정
        given(productQueryRepository.findTop5PopularProducts())
                .willReturn(fakeList);
    }

    @Test
    @DisplayName("getTop5PopularProducts() 호출 시 첫 번째는 repository, 두 번째는 캐시에서 값을 가져온다")
    void 인기상품_캐시_조회_성공() {
        List<PopularProductResponse> first = productService.getTop5PopularProducts();
        assertThat(first).isEqualTo(fakeList);
        then(productQueryRepository).should(times(1)).findTop5PopularProducts();

        List<PopularProductResponse> second = productService.getTop5PopularProducts();
        assertThat(second).isEqualTo(fakeList);
        then(productQueryRepository).shouldHaveNoMoreInteractions();
    }
}
