package kr.hhplus.be.server.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Tag(name = "Mock User", description = "사용자 잔액 관련 Mock API")
@RestController
@RequestMapping("/mock/users")
public class MockUserController {

    @Operation(summary = "잔액 충전(Mock)", description = "사용자 잔액을 충전하는 Mock API 입니다.")
    @PostMapping("/{userId}/charge")
    public ResponseEntity<String> mockCharge(
            @PathVariable Long userId,
            @RequestParam int amount
    ) {
        return ResponseEntity.ok("MOCK 충전 완료 : userId = " + userId + ", amount =" +amount);
    }

    @Operation(summary = "잔액 조회(Mock)", description = "사용자 잔액을 조회하는 Mock API 입니다.")
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Integer> mockBalance(@PathVariable Long userId) {
        int mockBalance = 45000;
        return ResponseEntity.ok(mockBalance);
    }
}
