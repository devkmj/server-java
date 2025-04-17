package kr.hhplus.be.server.domain.order.model;

import kr.hhplus.be.server.domain.balance.Balance;
import kr.hhplus.be.server.domain.order.exception.InsufficientBalanceException;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStatus;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.user.UserCoupon;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    public void validate(Product product, ProductStock stock, Balance balance, UserCoupon coupon, int qty, int totalPrice) {
        validateProductStatus(product);
        validateStock(stock, qty);
        validateBalance(balance, totalPrice);
        validateCoupon(coupon);
    }

    private void validateProductStatus(Product product) {
        if (product.getStatus() != ProductStatus.AVAILABLE && product.getStatus() != ProductStatus.ON_SALE) {
            throw new IllegalArgumentException("판매중인 상품이 아닙니다");
        }
    }

    private void validateStock(ProductStock stock, int qty) {
        if (!stock.hasEnough(qty)) {
            throw new IllegalArgumentException("상품 재고가 부족합니다");
        }
    }

    private void validateBalance(Balance balance, int totalPrice) {
        if (balance.getBalance() < totalPrice) {
            throw new InsufficientBalanceException("잔액이 부족합니다");
        }
    }

    private void validateCoupon(UserCoupon coupon) {
        if (coupon == null) return;

        if (coupon.isUsed()) {
            throw new IllegalArgumentException("이미 사용된 쿠폰입니다.");
        }

        if (!coupon.getCoupon().isValidNow()) {
            throw new IllegalArgumentException("유효하지 않은 쿠폰입니다.");
        }
    }
}