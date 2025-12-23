package com.autocat.playground.springboot4features;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

/**
 * Spring Boot 4.0 - Observability (관찰성) 개선
 *
 * Micrometer Observation API를 통한 통합 관찰성 기능이 더욱 강화되었습니다.
 * 메트릭, 트레이싱, 로깅을 하나의 API로 통합 관리합니다.
 *
 * 주요 개선사항:
 * - @Observed 어노테이션 개선
 * - 자동 구성 강화
 * - Virtual Threads 컨텍스트 전파
 * - 새로운 관찰 규약 추가
 * - OpenTelemetry 통합 개선
 *
 * application.properties 설정:
 * ```
 * # Observability 활성화
 * management.observations.http.server.requests.enabled=true
 * management.observations.http.client.requests.enabled=true
 *
 * # Tracing 설정
 * management.tracing.enabled=true
 * management.tracing.sampling.probability=1.0
 *
 * # Zipkin 설정
 * management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
 * ```
 */
@Service
public class ObservabilityExample {

    private final ObservationRegistry observationRegistry;
    private final Random random = new Random();

    public ObservabilityExample(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    /**
     * @Observed 어노테이션 사용
     * 메서드 실행을 자동으로 관찰합니다.
     */
    @Observed(
            name = "user.fetch",
            contextualName = "fetching-user",
            lowCardinalityKeyValues = {"type", "database"}
    )
    public String fetchUser(Long userId) {
        // 메서드 실행 시간, 성공/실패 등이 자동으로 기록됨
        simulateDelay();
        return "User-" + userId;
    }

    /**
     * 수동 Observation 생성
     * 더 세밀한 제어가 필요할 때 사용
     */
    public String processOrder(String orderId) {
        // Observation 시작
        Observation observation = Observation.createNotStarted("order.process", observationRegistry)
                .lowCardinalityKeyValue("order.type", "standard")
                .highCardinalityKeyValue("order.id", orderId);

        return observation.observe(() -> {
            // 비즈니스 로직
            simulateDelay();

            // 추가 컨텍스트 설정
            observation.event(Observation.Event.of("order.validated", "Order validation completed"));

            simulateDelay();

            observation.event(Observation.Event.of("order.processed", "Order processing completed"));

            return "Processed: " + orderId;
        });
    }

    /**
     * 에러 기록과 함께 관찰
     */
    public String riskyOperation(String input) {
        Observation observation = Observation.createNotStarted("risky.operation", observationRegistry)
                .lowCardinalityKeyValue("input.type", input.getClass().getSimpleName());

        try {
            return observation.observeChecked(() -> {
                if (random.nextBoolean()) {
                    throw new RuntimeException("Random failure!");
                }
                return "Success: " + input;
            });
        } catch (Throwable e) {
            // 에러가 자동으로 Observation에 기록됨
            throw new RuntimeException(e);
        }
    }

    /**
     * 중첩 Observation (부모-자식 관계)
     */
    public void complexOperation() {
        Observation parentObservation = Observation.start("complex.operation", observationRegistry);

        try (Observation.Scope parentScope = parentObservation.openScope()) {
            // 부모 Observation 범위 내에서 자식 Observation 생성
            processStep1();
            processStep2();
            processStep3();
        } finally {
            parentObservation.stop();
        }
    }

    private void processStep1() {
        Observation.createNotStarted("step1", observationRegistry)
                .observe(() -> {
                    simulateDelay();
                });
    }

    private void processStep2() {
        Observation.createNotStarted("step2", observationRegistry)
                .observe(() -> {
                    simulateDelay();
                });
    }

    private void processStep3() {
        Observation.createNotStarted("step3", observationRegistry)
                .observe(() -> {
                    simulateDelay();
                });
    }

    /**
     * Virtual Threads와 함께 사용
     * Spring Boot 4.0에서는 Virtual Threads 간 컨텍스트 전파가 자동으로 처리됨
     */
    public void observeWithVirtualThreads() throws InterruptedException {
        Observation observation = Observation.start("virtual.thread.operation", observationRegistry);

        try (Observation.Scope scope = observation.openScope()) {
            // Virtual Thread에서도 Observation 컨텍스트가 전파됨
            Thread.startVirtualThread(() -> {
                // 이 Virtual Thread 내에서도 부모 Observation 컨텍스트 사용 가능
                Observation childObservation = Observation.createNotStarted(
                        "virtual.thread.child",
                        observationRegistry
                );
                childObservation.observe(() -> {
                    System.out.println("Virtual Thread 내에서 실행");
                });
            }).join();
        } finally {
            observation.stop();
        }
    }

    /**
     * 커스텀 키-값 쌍 추가
     */
    @Observed(name = "payment.process")
    public void processPayment(String paymentId, double amount, String currency) {
        // High cardinality: 고유한 값들 (예: ID, 금액)
        // Low cardinality: 제한된 값들 (예: 통화 종류, 상태)

        Observation.createNotStarted("payment.process", observationRegistry)
                .lowCardinalityKeyValue("payment.currency", currency)
                .lowCardinalityKeyValue("payment.status", "processing")
                .highCardinalityKeyValue("payment.id", paymentId)
                .highCardinalityKeyValue("payment.amount", String.valueOf(amount))
                .observe(() -> {
                    simulateDelay();
                });
    }

    private void simulateDelay() {
        try {
            Thread.sleep(Duration.ofMillis(50 + random.nextInt(100)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
