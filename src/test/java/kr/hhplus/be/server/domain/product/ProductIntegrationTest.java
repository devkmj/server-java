package kr.hhplus.be.server.domain.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.entity.ProductStatus;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Rollback
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Product 통합 테스트")
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.saveAll(List.of(
                new Product("맥북", 1500000, ProductStatus.ON_SALE),
                new Product("아이패드", 1000000, ProductStatus.AVAILABLE)
        ));
    }

    @Test
    @DisplayName("전체 상품 목록 조회 성공")
    void 전체_상품_조회() throws Exception {
        mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("맥북"))
                .andExpect(jsonPath("$.data[1].name").value("아이패드"));
    }

    @Test
    @DisplayName("상품 ID로 조회 성공")
    void 상품_ID_조회() throws Exception {
        Long id = productRepository.findAll().get(0).getId();

        mockMvc.perform(get("/products/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.name").value("맥북"));
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID 조회 시 404 반환")
    void 상품_ID_조회_예외() throws Exception {
        mockMvc.perform(get("/products/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("상품 조회 실패"))
                .andExpect(jsonPath("$.data").value("존재하지 않는 상품입니다."));
    }

    @Test
    @DisplayName("GET /popular → 인기 상품 5개 반환")
    void getPopularProducts() throws Exception {
        // when / then
        mockMvc.perform(get("/products/popular")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("인기 상품 조회 성공"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productId").value(anyLong()))
                .andExpect(jsonPath("$.data[0].name").value(anyString()))
                .andExpect(jsonPath("$.data[1].totalSold").value(anyInt()));
    }
}
