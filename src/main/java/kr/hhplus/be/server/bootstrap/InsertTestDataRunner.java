package kr.hhplus.be.server.bootstrap;

import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository;
import kr.hhplus.be.server.domain.product.service.ProductSalesSummaryService;
import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Profile("test") // 꼭 local 환경에서만 실행되게!
public class InsertTestDataRunner implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductSalesSummaryRepository productSalesSummaryRepository;
    private final ProductSalesSummaryService productSalesSummaryService;

    @Override
    public void run(String... args) {
        System.out.println("🔥 인기 상품용 더미 데이터 삽입 시작...");
        for (int i = 0; i <= 5; i++) {
            LocalDate soldAt = LocalDate.now().minusDays(i);
            java.util.List<ProductSalesSummary> batch = new java.util.ArrayList<>();
            for (int j = 0; j <= 1000; j++) {
                Product product = productRepository.save(
                        new Product("test-product-" + i, 1000 + j, ProductStatus.AVAILABLE)
                );
                int totalQty = ThreadLocalRandom.current().nextInt(1, 3000);
                ProductSalesSummary summary = new ProductSalesSummary(product.getId(), (long) totalQty, soldAt);
                batch.add(summary);
            }
            productSalesSummaryRepository.saveAll(batch);
            batch.clear();

            if (i % 1000 == 0) {
                System.out.println("생성 완료");
            }
        }

        System.out.println("✅ 더미 데이터 삽입 완료!");
    }
}