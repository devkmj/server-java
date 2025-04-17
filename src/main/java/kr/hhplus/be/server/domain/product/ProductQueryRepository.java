package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.application.product.PopularProductResponse;

import java.util.List;

public interface ProductQueryRepository {
    List<PopularProductResponse> findTop5PopularProducts();
}
