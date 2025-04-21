package kr.hhplus.be.server.bootstrap;

import kr.hhplus.be.server.domain.order.model.Order;
import kr.hhplus.be.server.domain.order.model.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.model.Product;
import kr.hhplus.be.server.domain.product.model.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.model.ProductStatus;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import kr.hhplus.be.server.domain.user.model.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("local") // 꼭 local 환경에서만 실행되게!
public class InsertTestDataRunner implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductSalesSummaryRepository productSalesSummaryRepository;

    @Override
    public void run(String... args) {
        System.out.println("🔥 인기 상품용 더미 데이터 삽입 시작...");

        User user = userRepository.save(new User("더미 유저"));

//        for (int i = 1; i <= 10000; i++) {
//            Product product = productRepository.save(
//                    new Product("테스트상품_" + i, 1000 + i, ProductStatus.AVAILABLE)
//            );
//
//            int qty = (i % 100) + 1; // 최대 100개씩 판매되었다고 가정
//            OrderItem item = new OrderItem(product, qty, product.getPrice());
//            Order order = Order.create(user, null, List.of(item), product.getPrice() * qty);
//            orderRepository.save(order);
//
//            if (i % 1000 == 0) {
//                System.out.println(i + "개 생성 완료");
//            }
//        }

//        for (int i = 1; i <= 10000; i++) {
//            Product product = productRepository.save(
//                    new Product("테스트상품_" + i, 1000 + i, ProductStatus.AVAILABLE)
//            );
//
//            long totalQty = (i % 100) + 1;  // 최대 100개까지 팔렸다고 가정
//            LocalDateTime soldAt = LocalDateTime.now().minusDays(i % 3);
//
//            ProductSalesSummary summary = new ProductSalesSummary(
//                    product.getId(), totalQty, soldAt
//            );
//            productSalesSummaryRepository.save(summary);
//
//            if (i % 1000 == 0) {
//                System.out.println(i + "개 생성 완료");
//            }
//        }
//
//
        System.out.println("✅ 더미 데이터 삽입 완료!");
    }
}