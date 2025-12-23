package com.autocat.playground.springboot4features;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot 4.0 - Configuration Properties 개선
 *
 * 설정 속성 바인딩이 더욱 강력하고 유연해졌습니다.
 *
 * 주요 개선사항:
 * - Record 클래스 완벽 지원
 * - Nested Configuration 개선
 * - 유효성 검증 통합 강화
 * - Default 값 처리 개선
 * - Duration, DataSize 등 타입 변환 강화
 *
 * application.yml 설정 예시:
 * ```yaml
 * app:
 *   name: My Application
 *   version: 1.0.0
 *   server:
 *     host: localhost
 *     port: 8080
 *     timeout: 30s
 *   features:
 *     caching: true
 *     async: true
 *   endpoints:
 *     - name: users
 *       url: https://api.example.com/users
 *       timeout: 10s
 *     - name: orders
 *       url: https://api.example.com/orders
 *       timeout: 15s
 * ```
 */

/**
 * Record 기반 Configuration Properties (Spring Boot 4.0 권장)
 */
@ConfigurationProperties(prefix = "app")
@Validated
public record ConfigurationPropertiesExample(
        @NotBlank String name,

        @DefaultValue("0.0.1-SNAPSHOT")
        String version,

        @DefaultValue("Production ready application")
        String description,

        ServerConfig server,

        @DefaultValue("true")
        Features features,

        List<Endpoint> endpoints,

        Map<String, String> metadata
) {

    /**
     * 중첩 설정 - 서버 구성
     */
    public record ServerConfig(
            @NotBlank
            @DefaultValue("localhost")
            String host,

            @Min(1) @Max(65535)
            @DefaultValue("8080")
            int port,

            @DefaultValue("30s")
            Duration timeout,

            @DefaultValue("10s")
            Duration connectionTimeout,

            @DefaultValue("100")
            int maxConnections,

            Ssl ssl
    ) {
        public record Ssl(
                @DefaultValue("false")
                boolean enabled,

                String keyStore,

                String keyStorePassword,

                @DefaultValue("JKS")
                String keyStoreType
        ) {
        }
    }

    /**
     * 중첩 설정 - 기능 플래그
     */
    public record Features(
            @DefaultValue("true")
            boolean caching,

            @DefaultValue("true")
            boolean async,

            @DefaultValue("false")
            boolean debug,

            @DefaultValue("true")
            boolean virtualThreads,

            @DefaultValue("false")
            boolean experimentalFeatures
    ) {
    }

    /**
     * 리스트 항목 설정
     */
    public record Endpoint(
            @NotBlank
            String name,

            @NotNull
            @Pattern(regexp = "^https?://.*")
            String url,

            @DefaultValue("10s")
            Duration timeout,

            @DefaultValue("3")
            int retryCount,

            Map<String, String> headers
    ) {
    }

    /**
     * 기본값이 적용된 record 생성
     * Spring Boot 4.0에서는 @DefaultValue가 더 잘 동작합니다.
     */
    public static ConfigurationPropertiesExample withDefaults() {
        return new ConfigurationPropertiesExample(
                "Default App",
                "1.0.0",
                "Default description",
                new ServerConfig(
                        "localhost",
                        8080,
                        Duration.ofSeconds(30),
                        Duration.ofSeconds(10),
                        100,
                        new ServerConfig.Ssl(false, null, null, "JKS")
                ),
                new Features(true, true, false, true, false),
                List.of(),
                Map.of()
        );
    }
}

/**
 * 클래스 기반 Configuration Properties (기존 방식도 지원)
 */
@ConfigurationProperties(prefix = "database")
@Validated
class DatabaseProperties {
    @NotBlank
    private String url;

    @NotBlank
    private String username;

    private String password;

    @Min(1)
    @Max(100)
    private int poolSize = 10;

    private Duration connectionTimeout = Duration.ofSeconds(30);

    private Duration idleTimeout = Duration.ofMinutes(10);

    private Map<String, String> properties = Map.of();

    // Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Duration getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
