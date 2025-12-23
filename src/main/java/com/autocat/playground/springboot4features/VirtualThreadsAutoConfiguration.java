package com.autocat.playground.springboot4features;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Spring Boot 4.0 - Virtual Threads 자동 구성
 *
 * Spring Boot 4.0에서는 Virtual Threads가 기본적으로 지원되며,
 * 간단한 설정만으로 활성화할 수 있습니다.
 *
 * application.properties 설정:
 * ```
 * # Virtual Threads 활성화 (Spring Boot 4.0에서는 기본값이 true)
 * spring.threads.virtual.enabled=true
 *
 * # Tomcat에서 Virtual Threads 사용
 * server.tomcat.threads.virtual=true
 * ```
 *
 * 주요 변경사항:
 * - spring.threads.virtual.enabled 속성 추가
 * - @Async 메서드에서 Virtual Threads 자동 사용
 * - Tomcat/Jetty에서 Virtual Threads 지원
 * - WebFlux와의 통합 개선
 */
@Configuration
@EnableAsync
public class VirtualThreadsAutoConfiguration {

    /**
     * Spring Boot 4.0에서 제공하는 VirtualThreadTaskExecutor
     * @Async 어노테이션과 함께 사용됩니다.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.threads.virtual.enabled", havingValue = "true", matchIfMissing = true)
    public VirtualThreadTaskExecutor virtualThreadTaskExecutor() {
        // Spring Boot 4.0에서 제공하는 Virtual Thread 기반 TaskExecutor
        return new VirtualThreadTaskExecutor("async-virtual-");
    }

    /**
     * Tomcat에서 Virtual Threads 사용하도록 커스터마이징
     * Spring Boot 4.0에서는 자동 구성되지만, 커스터마이징도 가능
     */
    @Bean
    @ConditionalOnProperty(name = "server.tomcat.threads.virtual", havingValue = "true")
    public TomcatProtocolHandlerCustomizer<?> virtualThreadsTomcatCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }

    /**
     * 일반적인 Virtual Thread ExecutorService Bean
     */
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
