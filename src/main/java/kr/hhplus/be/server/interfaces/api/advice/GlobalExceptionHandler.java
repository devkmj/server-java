package kr.hhplus.be.server.interfaces.api.advice;

import kr.hhplus.be.server.common.ApiResponse;
import kr.hhplus.be.server.domain.balance.exception.BalanceNotFoundException;
import kr.hhplus.be.server.domain.order.exception.InsufficientBalanceException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleBalanceNotFound(BalanceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("잔액 조회 실패", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("잘못된 요청입니다.");

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("요청 실패", message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.fail("요청이 잘못되었습니다", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiResponse<String>> handleBalanceNotFound(InsufficientBalanceException ex) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("잔액 부족", ex.getMessage()));
    }
}
