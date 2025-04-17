package kr.hhplus.be.server.interfaces.api.balance;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.balance.BalanceService;
import kr.hhplus.be.server.application.balance.dto.BalanceChargeCommand;
import kr.hhplus.be.server.application.balance.dto.BalanceResponse;
import kr.hhplus.be.server.common.ApiResponse;
import kr.hhplus.be.server.domain.balance.Balance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balances")
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(@PathVariable Long userId) {
        BalanceResponse response = balanceService.getBalance(userId);
        return ResponseEntity.ok(ApiResponse.success("잔액 조회 성공", response));
    }

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<BalanceResponse>> charge(@Valid @RequestBody BalanceChargeCommand command) {
        BalanceResponse response = balanceService.charge(command);
        return ResponseEntity.ok(ApiResponse.success("잔액 충전 성공", response));
    }
}
