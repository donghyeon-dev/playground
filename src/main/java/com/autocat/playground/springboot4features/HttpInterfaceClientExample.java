package com.autocat.playground.springboot4features;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.*;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

/**
 * Spring Boot 4.0 - HTTP Interface Client (@HttpExchange) 강화
 *
 * Spring 6에서 도입된 HTTP Interface가 Spring Boot 4.0에서 더욱 강화되었습니다.
 * RestClient 기반으로 선언적 HTTP 클라이언트를 생성할 수 있습니다.
 *
 * 주요 개선사항:
 * - RestClient 기반 어댑터 지원
 * - 에러 처리 개선
 * - 타임아웃 및 재시도 설정 간소화
 * - Virtual Threads와의 자연스러운 통합
 * - 새로운 어노테이션 추가
 */
@Configuration
public class HttpInterfaceClientExample {

    // DTO 정의
    public record User(Long id, String name, String email) {
    }

    public record CreateUserRequest(String name, String email) {
    }

    public record UpdateUserRequest(String name, String email) {
    }

    public record PageResponse<T>(List<T> content, int page, int size, long total) {
    }

    /**
     * HTTP Interface 정의
     * 인터페이스에 HTTP 요청을 선언적으로 정의합니다.
     */
    @HttpExchange(url = "/api/v1/users", accept = "application/json", contentType = "application/json")
    public interface UserClient {

        // GET 요청
        @GetExchange
        List<User> getAllUsers();

        // 페이징 지원
        @GetExchange
        PageResponse<User> getUsers(
                @RequestParam("page") int page,
                @RequestParam("size") int size
        );

        // 경로 변수 사용
        @GetExchange("/{id}")
        User getUserById(@PathVariable Long id);

        // POST 요청
        @PostExchange
        User createUser(@RequestBody CreateUserRequest request);

        // PUT 요청
        @PutExchange("/{id}")
        User updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request);

        // PATCH 요청 (Spring Boot 4.0 새로 추가)
        @PatchExchange("/{id}")
        User patchUser(@PathVariable Long id, @RequestBody UpdateUserRequest request);

        // DELETE 요청
        @DeleteExchange("/{id}")
        void deleteUser(@PathVariable Long id);

        // ResponseEntity 반환 (헤더, 상태 코드 접근 필요 시)
        @GetExchange("/{id}")
        ResponseEntity<User> getUserWithResponse(@PathVariable Long id);

        // 검색 기능
        @GetExchange("/search")
        List<User> searchUsers(
                @RequestParam(required = false) String name,
                @RequestParam(required = false) String email
        );
    }

    /**
     * Spring Boot 4.0에서 RestClient를 사용한 HTTP Interface 빈 생성
     */
    @Bean
    public UserClient userClient() {
        // RestClient 생성 (Spring Boot 4.0 권장 방식)
        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.example.com")
                .defaultHeader("Authorization", "Bearer token")
                .defaultHeader("X-API-Version", "v4")
                .build();

        // RestClientAdapter로 HTTP Interface 프록시 생성
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(UserClient.class);
    }

    /**
     * 에러 핸들러가 있는 RestClient 설정
     */
    @Bean
    public RestClient customRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.example.com")
                .defaultStatusHandler(
                        status -> status.is4xxClientError(),
                        (request, response) -> {
                            throw new RuntimeException("Client error: " + response.getStatusCode());
                        }
                )
                .defaultStatusHandler(
                        status -> status.is5xxServerError(),
                        (request, response) -> {
                            throw new RuntimeException("Server error: " + response.getStatusCode());
                        }
                )
                .build();
    }

    // 사용 예시
    public void exampleUsage(UserClient userClient) {
        // 모든 사용자 조회
        List<User> users = userClient.getAllUsers();

        // 단일 사용자 조회
        User user = userClient.getUserById(1L);

        // 사용자 생성
        User newUser = userClient.createUser(
                new CreateUserRequest("John", "john@example.com")
        );

        // 사용자 수정
        User updatedUser = userClient.updateUser(1L,
                new UpdateUserRequest("John Updated", "john.updated@example.com")
        );

        // 사용자 삭제
        userClient.deleteUser(1L);

        // ResponseEntity로 받기
        ResponseEntity<User> response = userClient.getUserWithResponse(1L);
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Headers: " + response.getHeaders());

        // 검색
        List<User> searchResults = userClient.searchUsers("John", null);
    }
}
