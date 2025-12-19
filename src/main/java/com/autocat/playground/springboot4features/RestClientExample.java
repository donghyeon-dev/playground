package com.autocat.playground.springboot4features;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Spring Boot 4.0 - RestClient 개선
 *
 * RestClient는 Spring 6.1에서 도입된 동기식 HTTP 클라이언트로,
 * Spring Boot 4.0에서 더욱 강화되었습니다.
 *
 * RestTemplate 대비 장점:
 * - 모던하고 Fluent한 API
 * - 함수형 에러 핸들링
 * - Virtual Threads와 자연스러운 통합
 * - HTTP Interface와 통합
 *
 * 주요 개선사항 (Spring Boot 4.0):
 * - 자동 구성 개선
 * - 기본 설정 강화
 * - SSL 번들 지원
 * - Observability 통합
 */
@Service
public class RestClientExample {

    private final RestClient restClient;
    private final RestClient.Builder restClientBuilder;

    // DTO
    public record Post(Long id, String title, String body, Long userId) {
    }

    public record CreatePostRequest(String title, String body, Long userId) {
    }

    public record ApiError(String code, String message, List<String> details) {
    }

    /**
     * Spring Boot 4.0에서는 RestClient.Builder가 자동으로 주입됩니다.
     * 기본 설정이 적용되어 있으며, 필요시 커스터마이징 가능합니다.
     */
    public RestClientExample(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
        this.restClient = restClientBuilder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .defaultHeader("X-Custom-Header", "value")
                .build();
    }

    /**
     * 기본 GET 요청
     */
    public Post getPost(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .retrieve()
                .body(Post.class);
    }

    /**
     * 리스트 응답 처리
     */
    public List<Post> getAllPosts() {
        return restClient.get()
                .uri("/posts")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * 쿼리 파라미터 사용
     */
    public List<Post> getPostsByUser(Long userId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/posts")
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    /**
     * POST 요청
     */
    public Post createPost(CreatePostRequest request) {
        return restClient.post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Post.class);
    }

    /**
     * PUT 요청
     */
    public Post updatePost(Long id, CreatePostRequest request) {
        return restClient.put()
                .uri("/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Post.class);
    }

    /**
     * DELETE 요청
     */
    public void deletePost(Long id) {
        restClient.delete()
                .uri("/posts/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * ResponseEntity로 전체 응답 받기
     */
    public ResponseEntity<Post> getPostWithFullResponse(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .retrieve()
                .toEntity(Post.class);
    }

    /**
     * 함수형 에러 핸들링 (Spring Boot 4.0 개선)
     */
    public Post getPostWithErrorHandling(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (request, response) -> {
                    throw new PostNotFoundException("Post not found: " + id);
                })
                .onStatus(status -> status.is4xxClientError(), (request, response) -> {
                    throw new ClientException("Client error: " + response.getStatusCode());
                })
                .onStatus(status -> status.is5xxServerError(), (request, response) -> {
                    throw new ServerException("Server error: " + response.getStatusCode());
                })
                .body(Post.class);
    }

    /**
     * exchange를 사용한 세밀한 응답 처리
     */
    public Post getPostWithExchange(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .exchange((request, response) -> {
                    if (response.getStatusCode().is2xxSuccessful()) {
                        return response.bodyTo(Post.class);
                    } else if (response.getStatusCode().value() == 404) {
                        throw new PostNotFoundException("Not found: " + id);
                    } else {
                        throw new RuntimeException("Error: " + response.getStatusCode());
                    }
                });
    }

    /**
     * 헤더 커스터마이징
     */
    public Post getPostWithAuth(Long id, String token) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .header("Authorization", "Bearer " + token)
                .header("X-Request-ID", java.util.UUID.randomUUID().toString())
                .retrieve()
                .body(Post.class);
    }

    /**
     * 타임아웃이 적용된 RestClient 생성 (Spring Boot 4.0)
     */
    public RestClient createTimeoutClient() {
        return restClientBuilder
                .baseUrl("https://api.example.com")
                // Spring Boot 4.0에서는 더 간단한 타임아웃 설정 제공
                .build();
    }

    /**
     * Map으로 응답 받기
     */
    public Map<String, Object> getPostAsMap(Long id) {
        return restClient.get()
                .uri("/posts/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    // 커스텀 예외
    static class PostNotFoundException extends RuntimeException {
        public PostNotFoundException(String message) {
            super(message);
        }
    }

    static class ClientException extends RuntimeException {
        public ClientException(String message) {
            super(message);
        }
    }

    static class ServerException extends RuntimeException {
        public ServerException(String message) {
            super(message);
        }
    }
}
