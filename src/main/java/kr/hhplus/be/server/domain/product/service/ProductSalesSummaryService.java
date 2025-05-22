package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductSalesSummary;
import kr.hhplus.be.server.domain.product.repository.ProductSalesSummaryRepository; 
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProductSalesSummaryService {

    private final ProductSalesSummaryRepository productSalesSummaryRepository;

    public void addSalesSummary(Product product, int qty, LocalDate date) {
        productSalesSummaryRepository.findByProductIdAndOrderedAt(product.getId(), date)
                .ifPresentOrElse(
                        summary -> summary.increaseQty(qty),
                        () -> {
                            ProductSalesSummary newSummary = new ProductSalesSummary(
                                    product.getId(),
                                    (long) qty,
                                    date
                            );
                            productSalesSummaryRepository.save(newSummary);
                        }
                );
    }
}
