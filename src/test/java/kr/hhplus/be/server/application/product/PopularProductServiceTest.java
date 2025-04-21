package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.model.ProductStatus;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
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
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("최근 3일간 주문 데이터를 기준으로 인기 상품 Top5를 조회한다")
    void 인기상품_조회() {
        // given
        Product product1 = productRepository.save(new Product("product1", 10000, ProductStatus.AVAILABLE));
        Product product2 = productRepository.save(new Product("product2", 10020, ProductStatus.AVAILABLE));
        Product product3 = productRepository.save(new Product("product3", 1300, ProductStatus.AVAILABLE));
        Product product4 = productRepository.save(new Product("product4", 40000, ProductStatus.AVAILABLE));
        Product product5 = productRepository.save(new Product("product5", 59000, ProductStatus.AVAILABLE));
        Product product6 = productRepository.save(new Product("product6", 8200, ProductStatus.AVAILABLE));

        User user = userRepository.save(new User("user"));

        saveOrder(user, product1, 130);
        saveOrder(user, product2, 121);
        saveOrder(user, product3, 22);
        saveOrder(user, product4, 803);
        saveOrder(user, product5, 14);

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
        OrderItem item = new OrderItem(product,qty , product.getPrice());
        Order order = Order.create(user, null, List.of(item), product.getPrice());
        orderRepository.save(order);
    }

    @Test
    @DisplayName("인기 상품 조회 - Summary 테이블 기반")
    void 인기_상품_요약_테이블_조회() {
        // given
        Product product = productRepository.save(new Product("summary-test", 10000, ProductStatus.AVAILABLE));
        productSalesSummaryRepository.save(new ProductSalesSummary(product.getId(), 500L, LocalDateTime.now())); // 500개 판매된 상품

        // when
        List<PopularProductResponse> result = productQueryRepository.findTop5PopularProducts();
        System.out.println("조회된 개수 = " + result.size());

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).productId()).isEqualTo(product.getId());
    }
}
