package com.autocat.playground.springboot4features;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Boot 4.0 - Spring Security 6.x 개선사항
 *
 * Spring Security가 대폭 개선되어 더 간결하고 안전한 보안 설정이 가능합니다.
 *
 * 주요 변경사항:
 * - Lambda DSL 기본 채택
 * - authorizeRequests() -> authorizeHttpRequests()
 * - antMatchers() -> requestMatchers()
 * - WebSecurityConfigurerAdapter 완전 제거
 * - @PreAuthorize, @PostAuthorize 개선
 * - OAuth2 Client 개선
 * - 패스키(Passkey/WebAuthn) 지원
 *
 * application.properties 설정:
 * ```
 * # OAuth2 설정
 * spring.security.oauth2.client.registration.google.client-id=your-client-id
 * spring.security.oauth2.client.registration.google.client-secret=your-secret
 *
 * # JWT 설정
 * spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.example.com
 * ```
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // @PreAuthorize 등 활성화
public class SecurityEnhancementsExample {

    /**
     * Spring Security 6.x 스타일의 SecurityFilterChain 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Lambda DSL 사용 (Spring Security 6.x 권장)
                .authorizeHttpRequests(authorize -> authorize
                        // 공개 엔드포인트
                        .requestMatchers("/", "/public/**", "/health", "/actuator/health").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()

                        // Swagger/API 문서
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 관리자 전용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasAuthority("SCOPE_admin")

                        // API 엔드포인트 - 인증 필요
                        .requestMatchers("/api/v1/**").authenticated()

                        // 그 외 모든 요청 - 인증 필요
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                        .failureUrl("/login?error")
                )

                // OAuth2 리소스 서버 (JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                // JWT 커스터마이징
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )

                // 세션 관리
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )

                // CSRF 설정
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")  // API는 CSRF 비활성화
                )

                // CORS 설정
                .cors(cors -> cors
                        .configurationSource(request -> {
                            var config = new org.springframework.web.cors.CorsConfiguration();
                            config.setAllowedOrigins(java.util.List.of("https://example.com"));
                            config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE"));
                            config.setAllowedHeaders(java.util.List.of("*"));
                            config.setAllowCredentials(true);
                            return config;
                        })
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )

                // 예외 처리
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(401, "Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(403, "Access Denied");
                        })
                );

        return http.build();
    }

    /**
     * JWT 인증 컨버터 커스터마이징
     */
    private org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
    jwtAuthenticationConverter() {
        var converter = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();

        // 권한 추출 커스터마이징
        var authoritiesConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    /**
     * 비밀번호 인코더
     */
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.factory.PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }
}

/**
 * Method Security 예시
 * @PreAuthorize, @PostAuthorize 등을 활용한 메서드 수준 보안
 */
// @Service
// class SecuredService {
//
//     @PreAuthorize("hasRole('ADMIN')")
//     public void adminOnlyMethod() {
//         // 관리자만 접근 가능
//     }
//
//     @PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
//     public UserData getUserData(Long userId) {
//         // 자신의 데이터만 접근 가능
//         return new UserData(userId, "data");
//     }
//
//     @PostAuthorize("returnObject.ownerId == authentication.principal.id")
//     public Document getDocument(Long docId) {
//         // 반환된 문서의 소유자인 경우만 허용
//         return new Document(docId, 1L);
//     }
//
//     @PreAuthorize("@authService.canAccess(#resourceId)")
//     public Resource getResource(Long resourceId) {
//         // 커스텀 권한 체크
//         return new Resource(resourceId);
//     }
//
//     record UserData(Long userId, String data) {}
//     record Document(Long id, Long ownerId) {}
//     record Resource(Long id) {}
// }
