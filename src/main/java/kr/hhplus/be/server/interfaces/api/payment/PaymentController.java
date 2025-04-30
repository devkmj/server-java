package kr.hhplus.be.server.interfaces.api.payment;

import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.interfaces.api.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentFacade paymentFacade;

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<ApiResponse> pay(
            @PathVariable Long orderId
    ) {
        paymentFacade.completePayment(orderId);
        return ResponseEntity.ok(ApiResponse.success("결제 완료"));
    }
}
