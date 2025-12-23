package com.autocat.playground.springboot4features;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

/**
 * Spring Boot 4.0 - Problem Details (RFC 7807/9457) 지원 강화
 *
 * API 에러 응답을 표준화된 형식으로 반환합니다.
 * Spring Boot 4.0에서는 ProblemDetail이 기본 에러 응답 형식입니다.
 *
 * RFC 9457 (RFC 7807의 후속) 표준 필드:
 * - type: 에러 유형을 식별하는 URI
 * - title: 에러의 짧은 설명
 * - status: HTTP 상태 코드
 * - detail: 구체적인 에러 설명
 * - instance: 에러가 발생한 리소스 URI
 *
 * application.properties 설정:
 * ```
 * # Problem Details 활성화 (Spring Boot 4.0에서는 기본값)
 * spring.mvc.problemdetails.enabled=true
 * ```
 */
@RestController
@RequestMapping("/api/v1/orders")
public class ProblemDetailsExample {

    public record Order(Long id, String productName, int quantity, double price) {
    }

    /**
     * ProblemDetail을 직접 반환하는 방식
     */
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        if (id < 0) {
            // ProblemDetail 직접 생성
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    "Order ID must be a positive number"
            );
            problemDetail.setType(URI.create("https://api.example.com/errors/invalid-order-id"));
            problemDetail.setTitle("Invalid Order ID");
            problemDetail.setInstance(URI.create("/api/v1/orders/" + id));

            // 커스텀 필드 추가
            problemDetail.setProperty("orderId", id);
            problemDetail.setProperty("timestamp", Instant.now());

            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, problemDetail, null);
        }

        if (id > 1000) {
            throw new OrderNotFoundException(id);
        }

        return new Order(id, "Sample Product", 1, 99.99);
    }

    /**
     * 커스텀 예외 with ProblemDetail
     */
    public static class OrderNotFoundException extends ErrorResponseException {
        private final Long orderId;

        public OrderNotFoundException(Long orderId) {
            super(HttpStatus.NOT_FOUND, createProblemDetail(orderId), null);
            this.orderId = orderId;
        }

        private static ProblemDetail createProblemDetail(Long orderId) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    "Order with ID " + orderId + " was not found in the system"
            );
            pd.setType(URI.create("https://api.example.com/errors/order-not-found"));
            pd.setTitle("Order Not Found");
            pd.setProperty("orderId", orderId);
            pd.setProperty("suggestion", "Please verify the order ID and try again");
            return pd;
        }

        public Long getOrderId() {
            return orderId;
        }
    }

    /**
     * 유효성 검증 에러 처리
     */
    @PostMapping
    public Order createOrder(@RequestBody CreateOrderRequest request) {
        if (request.quantity() <= 0) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    "Quantity must be greater than zero"
            );
            pd.setType(URI.create("https://api.example.com/errors/validation-error"));
            pd.setTitle("Validation Error");
            pd.setProperty("field", "quantity");
            pd.setProperty("rejectedValue", request.quantity());
            pd.setProperty("constraint", "must be > 0");

            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, pd, null);
        }

        return new Order(1L, request.productName(), request.quantity(), request.price());
    }

    public record CreateOrderRequest(String productName, int quantity, double price) {
    }

    /**
     * 비즈니스 규칙 위반 에러
     */
    public static class InsufficientStockException extends ErrorResponseException {
        public InsufficientStockException(String productName, int requested, int available) {
            super(HttpStatus.CONFLICT, createProblemDetail(productName, requested, available), null);
        }

        private static ProblemDetail createProblemDetail(String productName, int requested, int available) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.CONFLICT,
                    "Insufficient stock for product: " + productName
            );
            pd.setType(URI.create("https://api.example.com/errors/insufficient-stock"));
            pd.setTitle("Insufficient Stock");
            pd.setProperty("productName", productName);
            pd.setProperty("requestedQuantity", requested);
            pd.setProperty("availableQuantity", available);
            pd.setProperty("actions", Map.of(
                    "reduce", "Try ordering a smaller quantity",
                    "notify", "Sign up for restock notifications"
            ));
            return pd;
        }
    }

    /**
     * 글로벌 예외 핸들러에서 ProblemDetail 사용
     */
    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException.class)
        public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    ex.getMessage()
            );
            pd.setType(URI.create("https://api.example.com/errors/invalid-argument"));
            pd.setTitle("Invalid Argument");
            pd.setProperty("timestamp", Instant.now());
            return pd;
        }

        @ExceptionHandler(Exception.class)
        public ProblemDetail handleGenericException(Exception ex) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An unexpected error occurred"
            );
            pd.setType(URI.create("https://api.example.com/errors/internal-error"));
            pd.setTitle("Internal Server Error");
            pd.setProperty("timestamp", Instant.now());
            // 프로덕션에서는 상세 에러 메시지를 숨김
            // pd.setProperty("debugMessage", ex.getMessage());
            return pd;
        }
    }
}
