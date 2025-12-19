package com.autocat.playground.java25features;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;

/**
 * Java 21+ - Structured Concurrency (구조적 동시성)
 *
 * 동시 작업을 구조화된 방식으로 관리합니다.
 * 부모-자식 관계의 작업 그룹을 생성하고, 모든 작업이 완료되거나
 * 하나가 실패하면 나머지를 자동으로 취소합니다.
 *
 * 주요 특징:
 * - 작업 그룹의 생명주기 관리
 * - 실패 시 자동 취소 (fail-fast)
 * - 리소스 누수 방지
 * - Virtual Threads와 자연스러운 통합
 *
 * 참고: Java 21에서 Preview, Java 25에서 정식 기능 예정
 */
public class StructuredConcurrencyExample {

    record User(String id, String name) {
    }

    record Order(String orderId, String userId) {
    }

    record ShippingInfo(String orderId, String address) {
    }

    record OrderDetails(User user, Order order, ShippingInfo shipping) {
    }

    /**
     * 기본 사용법 - ShutdownOnFailure
     * 하나라도 실패하면 나머지 작업 취소
     */
    public OrderDetails fetchOrderDetails(String orderId) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // 병렬로 세 가지 작업 시작
            Subtask<User> userTask = scope.fork(() -> fetchUser("user123"));
            Subtask<Order> orderTask = scope.fork(() -> fetchOrder(orderId));
            Subtask<ShippingInfo> shippingTask = scope.fork(() -> fetchShipping(orderId));

            // 모든 작업 완료 대기 (또는 하나라도 실패 시 즉시 반환)
            scope.join();

            // 실패한 작업이 있으면 예외 발생
            scope.throwIfFailed();

            // 모든 결과 조합
            return new OrderDetails(
                    userTask.get(),
                    orderTask.get(),
                    shippingTask.get()
            );
        }
    }

    /**
     * ShutdownOnSuccess - 하나라도 성공하면 나머지 취소
     * 가장 빠른 결과만 필요할 때 사용
     */
    public String fetchFromFastestMirror(List<String> mirrorUrls) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
            // 여러 미러에 동시 요청
            for (String url : mirrorUrls) {
                scope.fork(() -> fetchFromUrl(url));
            }

            // 첫 번째 성공 결과 대기 (나머지는 자동 취소)
            scope.join();

            // 가장 빠른 결과 반환
            return scope.result();
        }
    }

    /**
     * 타임아웃 설정
     */
    public OrderDetails fetchWithTimeout(String orderId, Duration timeout) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<User> userTask = scope.fork(() -> fetchUser("user123"));
            Subtask<Order> orderTask = scope.fork(() -> fetchOrder(orderId));
            Subtask<ShippingInfo> shippingTask = scope.fork(() -> fetchShipping(orderId));

            // 타임아웃과 함께 대기
            scope.joinUntil(Instant.now().plus(timeout));
            scope.throwIfFailed();

            return new OrderDetails(
                    userTask.get(),
                    orderTask.get(),
                    shippingTask.get()
            );
        }
    }

    /**
     * 사용자 정의 정책 - 부분 실패 허용
     * 일부 작업이 실패해도 계속 진행
     */
    public record PartialResult<T>(T value, Exception error) {
        public boolean isSuccess() {
            return error == null;
        }
    }

    public List<PartialResult<String>> fetchAllWithPartialFailure(List<String> urls) throws Exception {
        try (var scope = new StructuredTaskScope<PartialResult<String>>()) {
            List<Subtask<PartialResult<String>>> tasks = urls.stream()
                    .map(url -> scope.fork(() -> {
                        try {
                            return new PartialResult<>(fetchFromUrl(url), null);
                        } catch (Exception e) {
                            return new PartialResult<>(null, e);
                        }
                    }))
                    .toList();

            scope.join();

            return tasks.stream()
                    .map(Subtask::get)
                    .toList();
        }
    }

    /**
     * 중첩된 구조적 동시성
     */
    public void nestedStructuredConcurrency() throws Exception {
        try (var outerScope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<String> task1 = outerScope.fork(() -> {
                // 내부에서 또 다른 구조적 동시성 사용
                try (var innerScope = new StructuredTaskScope.ShutdownOnFailure()) {
                    var subTask1 = innerScope.fork(() -> "SubTask1 결과");
                    var subTask2 = innerScope.fork(() -> "SubTask2 결과");

                    innerScope.join();
                    innerScope.throwIfFailed();

                    return subTask1.get() + " + " + subTask2.get();
                }
            });

            Subtask<String> task2 = outerScope.fork(() -> "Task2 직접 결과");

            outerScope.join();
            outerScope.throwIfFailed();

            System.out.println("Task1: " + task1.get());
            System.out.println("Task2: " + task2.get());
        }
    }

    /**
     * Virtual Threads와 통합
     */
    public void withVirtualThreads() throws Exception {
        // StructuredTaskScope은 내부적으로 Virtual Threads 사용
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // 수천 개의 동시 작업도 효율적으로 처리
            for (int i = 0; i < 1000; i++) {
                final int taskId = i;
                scope.fork(() -> {
                    Thread.sleep(Duration.ofMillis(100));
                    return "Task " + taskId + " on " + Thread.currentThread();
                });
            }

            scope.join();
            scope.throwIfFailed();
            System.out.println("1000개 작업 완료");
        }
    }

    /**
     * 취소 처리
     */
    public void handleCancellation() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<String> task1 = scope.fork(() -> {
                try {
                    Thread.sleep(Duration.ofSeconds(10));
                    return "완료";
                } catch (InterruptedException e) {
                    System.out.println("Task1 취소됨");
                    throw e;
                }
            });

            Subtask<String> task2 = scope.fork(() -> {
                Thread.sleep(Duration.ofMillis(100));
                throw new RuntimeException("의도적 실패");
            });

            try {
                scope.join();
                scope.throwIfFailed();
            } catch (Exception e) {
                // task2가 실패하면 task1은 자동 취소됨
                System.out.println("예외 발생: " + e.getMessage());
            }
        }
    }

    // 시뮬레이션 메서드들
    private User fetchUser(String userId) throws InterruptedException {
        Thread.sleep(100);
        return new User(userId, "John Doe");
    }

    private Order fetchOrder(String orderId) throws InterruptedException {
        Thread.sleep(150);
        return new Order(orderId, "user123");
    }

    private ShippingInfo fetchShipping(String orderId) throws InterruptedException {
        Thread.sleep(120);
        return new ShippingInfo(orderId, "123 Main St");
    }

    private String fetchFromUrl(String url) throws InterruptedException {
        Thread.sleep((long) (Math.random() * 500));
        return "Response from " + url;
    }

    public static void main(String[] args) throws Exception {
        StructuredConcurrencyExample example = new StructuredConcurrencyExample();

        // 주문 상세 조회
        OrderDetails details = example.fetchOrderDetails("order-001");
        System.out.println("주문 상세: " + details);

        // 가장 빠른 미러에서 가져오기
        List<String> mirrors = List.of(
                "https://mirror1.example.com",
                "https://mirror2.example.com",
                "https://mirror3.example.com"
        );
        String result = example.fetchFromFastestMirror(mirrors);
        System.out.println("가장 빠른 결과: " + result);
    }
}
