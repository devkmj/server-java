package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.interfaces.api.product.response.PopularProductResponse;

import java.util.List;

public interface ProductQueryRepository {
    List<PopularProductResponse> findTop5PopularProducts();
}
