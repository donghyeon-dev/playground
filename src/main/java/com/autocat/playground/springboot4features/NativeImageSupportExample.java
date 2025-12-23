package com.autocat.playground.springboot4features;

import org.springframework.aot.hint.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.List;

/**
 * Spring Boot 4.0 - GraalVM Native Image 지원 강화
 *
 * 네이티브 이미지 빌드가 더욱 안정적이고 쉬워졌습니다.
 * Spring AOT (Ahead-of-Time) 처리가 개선되었습니다.
 *
 * 주요 개선사항:
 * - 자동 리플렉션 힌트 생성 개선
 * - 런타임 힌트 등록 간소화
 * - 네이티브 이미지 빌드 시간 단축
 * - 메모리 사용량 최적화
 * - 시작 시간 개선 (밀리초 단위 시작)
 *
 * 네이티브 빌드 명령:
 * ```
 * ./gradlew nativeCompile
 * ./gradlew nativeTest
 * ./gradlew bootBuildImage
 * ```
 *
 * build.gradle 설정:
 * ```
 * plugins {
 *     id 'org.graalvm.buildtools.native' version '0.10.4'
 * }
 *
 * graalvmNative {
 *     binaries {
 *         main {
 *             imageName = 'my-native-app'
 *             quickBuild = true  // 개발 시 빠른 빌드
 *         }
 *     }
 * }
 * ```
 */
@Configuration
@ImportRuntimeHints(NativeImageSupportExample.AppRuntimeHints.class)
public class NativeImageSupportExample {

    /**
     * 런타임 힌트 등록기
     * 네이티브 이미지에서 리플렉션이 필요한 클래스들을 등록합니다.
     */
    static class AppRuntimeHints implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // 리플렉션 힌트 등록
            hints.reflection()
                    // 클래스 전체 리플렉션 허용
                    .registerType(UserDto.class, MemberCategory.values())
                    .registerType(OrderDto.class, MemberCategory.values())
                    // 특정 멤버만 허용
                    .registerType(ConfigDto.class,
                            MemberCategory.PUBLIC_FIELDS,
                            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                            MemberCategory.INVOKE_PUBLIC_METHODS);

            // 리소스 힌트 등록
            hints.resources()
                    .registerPattern("templates/*.html")
                    .registerPattern("static/**")
                    .registerPattern("messages/*.properties");

            // 프록시 힌트 등록 (JDK 동적 프록시)
            hints.proxies()
                    .registerJdkProxy(UserService.class)
                    .registerJdkProxy(OrderService.class);

            // 직렬화 힌트 등록
            hints.serialization()
                    .registerType(UserDto.class)
                    .registerType(OrderDto.class);
        }
    }

    /**
     * @RegisterReflectionForBinding 어노테이션 사용
     * JSON 바인딩에 필요한 리플렉션을 자동 등록합니다.
     */
    // @RegisterReflectionForBinding({UserDto.class, OrderDto.class})
    // 위 어노테이션을 메서드나 클래스에 붙이면 자동으로 리플렉션 힌트 등록

    /**
     * 네이티브 이미지 최적화를 위한 DTO
     * record는 네이티브 이미지와 잘 호환됩니다.
     */
    public record UserDto(
            Long id,
            String name,
            String email,
            List<String> roles
    ) {
    }

    public record OrderDto(
            Long id,
            Long userId,
            String productName,
            int quantity,
            double totalPrice
    ) {
    }

    public record ConfigDto(
            String key,
            String value,
            boolean enabled
    ) {
    }

    /**
     * 서비스 인터페이스 (프록시용)
     */
    public interface UserService {
        UserDto findById(Long id);

        List<UserDto> findAll();

        UserDto save(UserDto user);
    }

    public interface OrderService {
        OrderDto findById(Long id);

        List<OrderDto> findByUserId(Long userId);

        OrderDto create(OrderDto order);
    }

    /**
     * 네이티브 이미지에서의 성능 특성
     *
     * JVM 모드 vs 네이티브 이미지:
     *
     * | 특성           | JVM        | Native     |
     * |---------------|------------|------------|
     * | 시작 시간      | 2-5초      | 50-200ms   |
     * | 메모리 사용    | 200-500MB  | 50-150MB   |
     * | 최대 처리량    | 높음       | 중간       |
     * | 웜업 필요      | 예         | 아니오     |
     * | 빌드 시간      | 빠름       | 느림       |
     *
     * 네이티브 이미지 적합한 경우:
     * - 서버리스 (AWS Lambda, Cloud Functions)
     * - 마이크로서비스
     * - CLI 도구
     * - 컨테이너 환경
     *
     * JVM 적합한 경우:
     * - 장기 실행 서비스
     * - 높은 처리량 필요
     * - 동적 기능 많이 사용
     */

    /**
     * 조건부 네이티브/JVM 코드
     */
    public void runtimeSpecificCode() {
        // GraalVM 네이티브 이미지에서 실행 중인지 확인
        boolean isNativeImage = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

        if (isNativeImage) {
            System.out.println("네이티브 이미지에서 실행 중");
            // 네이티브 이미지 특화 로직
        } else {
            System.out.println("JVM에서 실행 중");
            // JVM 특화 로직 (예: JIT 최적화 활용)
        }
    }
}
