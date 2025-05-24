package kr.hhplus.be.server.domain.order.entity;

public enum OrderStatus {
    PENDING,            // 주문 생성
    BALANCE_DEDUCTED,   // 잔액 차감 완료
    BALANCE_FAILED,     // 잔액 차감 실패
    INVENTORY_FAILED,   // 재고 부족
    FAILED,             // 주문 처리 실패
    CONFIRMED,          // 주문 확정 완료
    CANCELED,           // 주문 취소
    COMPLETED           // 주문 완료
}
