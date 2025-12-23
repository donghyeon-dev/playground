package com.autocat.playground.java25features;

import java.util.concurrent.StructuredTaskScope;

/**
 * Java 21+ - Scoped Values (범위 지정 값)
 *
 * ThreadLocal의 현대적 대안으로, 불변값을 스레드 및 자식 스레드와 공유합니다.
 * Virtual Threads와 Structured Concurrency와 함께 사용하도록 설계되었습니다.
 *
 * ThreadLocal 대비 장점:
 * - 불변성 보장 (값을 변경할 수 없음)
 * - 자동 범위 지정 (runWhere 블록 종료 시 자동 해제)
 * - 자식 스레드에 자동 상속
 * - 메모리 누수 방지
 * - Virtual Threads에 최적화
 *
 * 참고: Java 21에서 Preview, Java 25에서 정식 기능 예정
 */
public class ScopedValuesExample {

    // ScopedValue 선언 (final, static)
    private static final ScopedValue<String> CURRENT_USER = ScopedValue.newInstance();
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<SecurityContext> SECURITY_CONTEXT = ScopedValue.newInstance();

    record SecurityContext(String userId, String role, String token) {
    }

    /**
     * 기본 사용법 - runWhere
     */
    public void basicUsage() {
        // ScopedValue.runWhere로 값 바인딩
        ScopedValue.runWhere(CURRENT_USER, "admin", () -> {
            // 이 블록 내에서 CURRENT_USER 값에 접근 가능
            System.out.println("현재 사용자: " + CURRENT_USER.get());
            processRequest();
        });

        // 블록 밖에서는 값이 없음
        System.out.println("바인딩 해제됨, isBound: " + CURRENT_USER.isBound());
    }

    private void processRequest() {
        // 깊은 호출 스택에서도 접근 가능
        System.out.println("요청 처리 중 - 사용자: " + CURRENT_USER.get());
        validateUser();
    }

    private void validateUser() {
        // 파라미터로 전달하지 않아도 됨
        String user = CURRENT_USER.get();
        System.out.println("사용자 검증: " + user);
    }

    /**
     * 여러 ScopedValue 동시 바인딩
     */
    public void multipleBindings() {
        ScopedValue.runWhere(CURRENT_USER, "user123",
                () -> ScopedValue.runWhere(REQUEST_ID, "req-456", () -> {
                    System.out.println("사용자: " + CURRENT_USER.get());
                    System.out.println("요청 ID: " + REQUEST_ID.get());
                })
        );

        // 또는 Carrier 사용 (더 깔끔함)
        ScopedValue.where(CURRENT_USER, "user789")
                .where(REQUEST_ID, "req-012")
                .run(() -> {
                    System.out.println("사용자: " + CURRENT_USER.get());
                    System.out.println("요청 ID: " + REQUEST_ID.get());
                });
    }

    /**
     * callWhere - 값을 반환하는 경우
     */
    public String getUserGreeting(String username) {
        return ScopedValue.callWhere(CURRENT_USER, username, () -> {
            return "안녕하세요, " + CURRENT_USER.get() + "님!";
        });
    }

    /**
     * orElse - 바인딩되지 않은 경우 기본값
     */
    public void safeAccess() {
        // 바인딩되지 않은 경우 기본값 사용
        String user = CURRENT_USER.orElse("guest");
        System.out.println("현재 사용자 (기본값 적용): " + user);

        // orElseThrow - 바인딩 필수인 경우
        ScopedValue.runWhere(CURRENT_USER, "admin", () -> {
            String requiredUser = CURRENT_USER.orElseThrow(
                    () -> new IllegalStateException("사용자 컨텍스트가 필요합니다")
            );
            System.out.println("필수 사용자: " + requiredUser);
        });
    }

    /**
     * 중첩된 재바인딩
     */
    public void nestedRebinding() {
        ScopedValue.runWhere(CURRENT_USER, "outer-user", () -> {
            System.out.println("외부: " + CURRENT_USER.get());

            // 내부에서 다른 값으로 재바인딩
            ScopedValue.runWhere(CURRENT_USER, "inner-user", () -> {
                System.out.println("내부: " + CURRENT_USER.get());
            });

            // 다시 외부 값으로 복원
            System.out.println("외부로 복귀: " + CURRENT_USER.get());
        });
    }

    /**
     * Virtual Threads와 함께 사용
     */
    public void withVirtualThreads() throws InterruptedException {
        ScopedValue.runWhere(CURRENT_USER, "main-user", () -> {
            // Virtual Thread에서도 ScopedValue 상속
            Thread vThread = Thread.startVirtualThread(() -> {
                // 부모 스레드의 ScopedValue 값을 상속
                System.out.println("Virtual Thread 내 사용자: " + CURRENT_USER.get());
            });

            try {
                vThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Structured Concurrency와 함께 사용
     */
    public void withStructuredConcurrency() throws Exception {
        SecurityContext context = new SecurityContext("user123", "ADMIN", "token-xyz");

        ScopedValue.runWhere(SECURITY_CONTEXT, context, () -> {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
                // 모든 서브태스크가 SecurityContext를 상속
                var task1 = scope.fork(() -> {
                    System.out.println("Task1 - 역할: " + SECURITY_CONTEXT.get().role());
                    return "Task1 완료";
                });

                var task2 = scope.fork(() -> {
                    System.out.println("Task2 - 사용자: " + SECURITY_CONTEXT.get().userId());
                    return "Task2 완료";
                });

                scope.join().throwIfFailed();
                System.out.println(task1.get() + ", " + task2.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * ThreadLocal과 비교
     */
    public void compareWithThreadLocal() {
        // ThreadLocal 방식 (기존)
        ThreadLocal<String> threadLocalUser = new ThreadLocal<>();
        threadLocalUser.set("user-tl");
        try {
            System.out.println("ThreadLocal: " + threadLocalUser.get());
            // 값 변경 가능 (위험!)
            threadLocalUser.set("modified-user");
        } finally {
            threadLocalUser.remove();  // 수동으로 정리 필요 (안 하면 메모리 누수)
        }

        // ScopedValue 방식 (새로운 방식)
        ScopedValue.runWhere(CURRENT_USER, "user-sv", () -> {
            System.out.println("ScopedValue: " + CURRENT_USER.get());
            // 값 변경 불가 - 불변!
            // CURRENT_USER.set("modified") <- 이런 메서드 없음
        });
        // 자동으로 정리됨 - 메모리 누수 없음
    }

    public static void main(String[] args) {
        ScopedValuesExample example = new ScopedValuesExample();
        example.basicUsage();
        example.multipleBindings();
        System.out.println(example.getUserGreeting("Claude"));
        example.safeAccess();
        example.nestedRebinding();
    }
}
