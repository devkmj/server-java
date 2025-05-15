package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.repository.ProductQueryRepository;
import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularProductService {

    private final ProductQueryRepository productQueryRepository;

    @Cacheable("top5Last3d")
    public List<PopularProductResponse> getTop5PopularProducts() {
        return productQueryRepository.findTop5PopularProducts();
    }
}
