package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("ProductQueryRepository 테스트")
public class PopularProductServiceTest {

    @Autowired
    private ProductQueryRepository productQueryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSalesSummaryRepository productSalesSummaryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductSalesSummaryRepository summaryRepository;

    private Product product1, product2, product3, product4, product5, product6;

    @BeforeEach
    void setUp() {
        // 6개의 상품 생성
        product1 = productRepository.save(new Product("product1", 10000, ProductStatus.AVAILABLE));
        product2 = productRepository.save(new Product("product2", 10020, ProductStatus.AVAILABLE));
        product3 = productRepository.save(new Product("product3", 1300, ProductStatus.AVAILABLE));
        product4 = productRepository.save(new Product("product4", 40000, ProductStatus.AVAILABLE));
        product5 = productRepository.save(new Product("product5", 59000, ProductStatus.AVAILABLE));
        product6 = productRepository.save(new Product("product6", 8200, ProductStatus.AVAILABLE));

        User user = userRepository.save(new User("user"));

        // summary 테이블에 임의 판매량 기록
        summaryRepository.save(new ProductSalesSummary(product1.getId(), 130L, LocalDate.now()));
        summaryRepository.save(new ProductSalesSummary(product2.getId(), 121L, LocalDate.now()));
        summaryRepository.save(new ProductSalesSummary(product3.getId(), 22L, LocalDate.now()));
        summaryRepository.save(new ProductSalesSummary(product4.getId(), 803L, LocalDate.now()));
        summaryRepository.save(new ProductSalesSummary(product5.getId(), 14L, LocalDate.now()));
        summaryRepository.save(new ProductSalesSummary(product6.getId(), 4L, LocalDate.now()));
    }


    @Test
    @DisplayName("최근 3일간 주문 데이터를 기준으로 인기 상품 Top5를 조회한다")
    void 인기상품_조회() {
        // when
        List<PopularProductResponse> topProducts = productQueryRepository.findTop5PopularProducts();

        // then
        assertThat(topProducts).hasSize(5);
        List<Long> resultIds = topProducts.stream()
                .map(PopularProductResponse::productId)
                .toList();

        assertThat(resultIds).containsExactly(product4.getId(), product1.getId(), product2.getId(), product3.getId(), product5.getId());
    }

    private void saveOrder(User user, Product product, int qty) {
        List<UserCoupon> coupons = Collections.emptyList();
        OrderItem item = new OrderItem(product,qty , product.getPrice());
        Order order = Order.createPending(user, List.of(item), coupons, product.getPrice());
        orderRepository.save(order);
    }

    @Test
    @DisplayName("인기 상품 조회 - Summary 테이블 기반")
    void 인기_상품_요약_테이블_조회() {
        // given
        Product product = productRepository.save(new Product("summary-test", 10000, ProductStatus.AVAILABLE));
        productSalesSummaryRepository.save(new ProductSalesSummary(product.getId(), 5000L, LocalDate.now())); // 500개 판매된 상품

        // when
        List<PopularProductResponse> result = productQueryRepository.findTop5PopularProducts();
        System.out.println("조회된 개수 = " + result.size());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).productId()).isEqualTo(product.getId());
    }
}
