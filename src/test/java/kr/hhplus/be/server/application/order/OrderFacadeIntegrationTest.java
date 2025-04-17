package kr.hhplus.be.server.application.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.api.order.dto.OrderItemRequest;
import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.balance.BalanceRepository;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Order 통합 테스트")
public class OrderFacadeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductStockRepository productStockRepository;
    @Autowired
    private BalanceRepository balanceRepository;

    @BeforeEach
    void setUp() {
        // 데이터 초기화 (User, Product, Stock, Balance)
        User user = userRepository.save(new User("통합테스트유저"));
        Product product = productRepository.save(new Product("상품1", 10000, ProductStatus.AVAILABLE));
        productStockRepository.save(new ProductStock(product, 10));
        balanceRepository.save(new Balance(user.getId(), 50000));
    }

    @Test
    @DisplayName("정상 주문 요청 시 주문에 성공한다")
    void 주문_성공() throws Exception {
        // given
        Long userId = userRepository.findAll().get(0).getId();
        Product product = productRepository.findAll().get(0);
        int qty = 3;
        var orderRequest = new OrderRequest(userId, List.of(new OrderItemRequest(product.getId(), qty, product.getPrice())));

        // when & then
        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(orderRequest))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPrice").value(20000));

    }

}
