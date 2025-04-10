package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    public Product findById(Long invalidProductId) {
        return null;
    }


    public List<Product> getAllProducts() {
        return List.of();
    }
}
