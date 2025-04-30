package kr.hhplus.be.server.domain.order.entity;

public enum OrderStatus {
    CANCELD,    // 주문 취소
    COMPLETED,  // 주문 완료
    PENDING,    // 주문 생성
    FAILED,     // 주문 실패
    PAID,       // 결제 완료
    CONFIRMED   // 주문 성공 (재고 차감 완료)
}
