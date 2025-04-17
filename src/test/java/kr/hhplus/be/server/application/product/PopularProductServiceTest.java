package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductQueryRepository;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@DisplayName("ProductQueryRepository 테스트")
public class PopularProductServiceTest {

    @Autowired
    private ProductQueryRepository productQueryRepository;

    @Autowired
    private ProductRepository productRepository;

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
        Product product2 = productRepository.save(new Product("product2", 10000, ProductStatus.AVAILABLE));
        Product product3 = productRepository.save(new Product("product3", 10000, ProductStatus.AVAILABLE));
        Product product4 = productRepository.save(new Product("product4", 10000, ProductStatus.AVAILABLE));
        Product product5 = productRepository.save(new Product("product5", 10000, ProductStatus.AVAILABLE));
        Product product6 = productRepository.save(new Product("product6", 10000, ProductStatus.AVAILABLE));

        User user = userRepository.save(new User("user"));

        saveOrder(user, product1, 100);
        saveOrder(user, product1, 101);
        saveOrder(user, product1, 102);
        saveOrder(user, product1, 103);
        saveOrder(user, product1, 104);
        saveOrder(user, product1, 105);
        saveOrder(user, product1, 106);

        // when
        List<PopularProductResponse> result= productQueryRepository.findTop5PopularProducts();

        result.forEach(System.out::println);
        // then
    }

    private void saveOrder(User user, Product product, int qty) {
        OrderItem item = new OrderItem(product,100 , product.getPrice());
        Order order = Order.create(user, null, List.of(item), product.getPrice());
        orderRepository.save(order);
    }
}
