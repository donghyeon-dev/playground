package com.autocat.playground.springboot4features;

import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Spring Boot 4.0 - Actuator 엔드포인트 개선
 *
 * 모니터링 및 관리 기능이 더욱 강화되었습니다.
 *
 * 주요 개선사항:
 * - Virtual Threads 관련 메트릭 추가
 * - Kubernetes 프로브 개선
 * - 새로운 엔드포인트 추가
 * - 보안 설정 간소화
 * - Observability 통합 강화
 *
 * application.properties 설정:
 * ```
 * # 모든 엔드포인트 노출
 * management.endpoints.web.exposure.include=*
 *
 * # Health 상세 정보 표시
 * management.endpoint.health.show-details=always
 *
 * # Virtual Threads 메트릭
 * management.metrics.enable.jvm.threads.virtual=true
 * ```
 */
@Configuration
public class ActuatorEnhancementsExample {

    /**
     * 커스텀 Health Indicator
     * 애플리케이션의 특정 컴포넌트 상태를 체크합니다.
     */
    @Component
    public static class CustomDatabaseHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            // 데이터베이스 연결 체크 로직
            boolean isDatabaseUp = checkDatabaseConnection();

            if (isDatabaseUp) {
                return Health.up()
                        .withDetail("database", "PostgreSQL")
                        .withDetail("version", "16.0")
                        .withDetail("connections", 10)
                        .withDetail("maxConnections", 100)
                        .build();
            } else {
                return Health.down()
                        .withDetail("error", "Cannot connect to database")
                        .withDetail("lastAttempt", Instant.now())
                        .build();
            }
        }

        private boolean checkDatabaseConnection() {
            // 실제로는 데이터베이스 연결을 체크
            return true;
        }
    }

    /**
     * Virtual Threads Health Indicator (Spring Boot 4.0 신규)
     */
    @Component
    public static class VirtualThreadsHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            // Virtual Threads 지원 여부 확인
            boolean supportsVirtualThreads = true; // Java 21+

            return Health.up()
                    .withDetail("virtualThreadsSupported", supportsVirtualThreads)
                    .withDetail("virtualThreadsEnabled", true)
                    .withDetail("currentVirtualThreadCount", getVirtualThreadCount())
                    .build();
        }

        private long getVirtualThreadCount() {
            // 실제로는 JMX 등을 통해 가상 스레드 수 조회
            return Thread.getAllStackTraces().keySet().stream()
                    .filter(Thread::isVirtual)
                    .count();
        }
    }

    /**
     * 커스텀 Info Contributor
     * /actuator/info 엔드포인트에 추가 정보를 제공합니다.
     */
    @Component
    public static class CustomInfoContributor implements InfoContributor {

        @Override
        public void contribute(Builder builder) {
            builder.withDetail("application", Map.of(
                            "name", "Spring Boot 4.0 Demo",
                            "description", "Demonstrating Spring Boot 4.0 features",
                            "version", "4.0.0"
                    ))
                    .withDetail("java", Map.of(
                            "version", System.getProperty("java.version"),
                            "vendor", System.getProperty("java.vendor"),
                            "virtualThreadsEnabled", true
                    ))
                    .withDetail("runtime", Map.of(
                            "startTime", Instant.now().minusSeconds(3600),
                            "uptime", "1 hour"
                    ));
        }
    }

    /**
     * 커스텀 Actuator 엔드포인트
     * /actuator/custom 으로 접근 가능
     */
    @Component
    @Endpoint(id = "custom")
    public static class CustomEndpoint {

        private final Map<String, Object> data = new ConcurrentHashMap<>();
        private final AtomicLong requestCount = new AtomicLong(0);

        /**
         * GET /actuator/custom
         */
        @ReadOperation
        public Map<String, Object> getData() {
            requestCount.incrementAndGet();
            return Map.of(
                    "status", "active",
                    "data", data,
                    "requestCount", requestCount.get(),
                    "timestamp", Instant.now()
            );
        }

        /**
         * GET /actuator/custom/{key}
         */
        @ReadOperation
        public Object getByKey(@Selector String key) {
            return data.get(key);
        }

        /**
         * POST /actuator/custom
         */
        @WriteOperation
        public void setData(String key, String value) {
            data.put(key, value);
        }

        /**
         * DELETE /actuator/custom/{key}
         */
        @DeleteOperation
        public void deleteData(@Selector String key) {
            data.remove(key);
        }
    }

    /**
     * Web 전용 커스텀 엔드포인트
     */
    @Component
    @WebEndpoint(id = "webonly")
    public static class WebOnlyEndpoint {

        @ReadOperation
        public Map<String, Object> getInfo() {
            return Map.of(
                    "message", "This endpoint is only available via web",
                    "protocol", "HTTP",
                    "timestamp", Instant.now()
            );
        }
    }

    /**
     * 메트릭 관련 빈 설정
     */
    @Bean
    public io.micrometer.core.instrument.MeterRegistry.Config meterRegistryConfig(
            io.micrometer.core.instrument.MeterRegistry registry) {
        return registry.config()
                .commonTags("application", "spring-boot-4-demo")
                .commonTags("environment", "development");
    }
}
