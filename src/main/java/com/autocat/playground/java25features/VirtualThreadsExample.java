package com.autocat.playground.java25features;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Java 21 - Virtual Threads (가상 스레드)
 *
 * Virtual Threads는 경량 스레드로, 기존 플랫폼 스레드보다 훨씬 적은 리소스를 사용합니다.
 * 수백만 개의 동시 작업을 처리할 수 있으며, I/O 바운드 작업에 특히 유용합니다.
 *
 * 주요 특징:
 * - OS 스레드와 1:1 매핑되지 않음 (M:N 스케줄링)
 * - 블로킹 I/O 시 자동으로 다른 가상 스레드로 전환
 * - 기존 Thread API와 완벽하게 호환
 * - ExecutorService와 함께 사용 가능
 */
public class VirtualThreadsExample {

    /**
     * 가장 간단한 가상 스레드 생성 방법
     */
    public void basicVirtualThread() throws InterruptedException {
        // 1. Thread.startVirtualThread() 사용
        Thread vThread = Thread.startVirtualThread(() -> {
            System.out.println("Virtual Thread 실행 중: " + Thread.currentThread());
        });
        vThread.join();

        // 2. Thread.ofVirtual() 빌더 사용
        Thread namedVThread = Thread.ofVirtual()
                .name("my-virtual-thread")
                .start(() -> {
                    System.out.println("이름 있는 Virtual Thread: " + Thread.currentThread().getName());
                });
        namedVThread.join();
    }

    /**
     * Virtual Thread ExecutorService 사용
     * - 각 작업마다 새로운 가상 스레드 생성
     * - 수백만 개의 동시 작업도 처리 가능
     */
    public void virtualThreadExecutor() {
        // try-with-resources로 자동 종료
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            // 10,000개의 동시 작업 실행
            IntStream.range(0, 10_000).forEach(i ->
                    executor.submit(() -> {
                        // I/O 바운드 작업 시뮬레이션
                        try {
                            Thread.sleep(Duration.ofMillis(100));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return "Task " + i + " completed";
                    })
            );
        }
        // ExecutorService가 자동으로 종료되고 모든 작업 완료 대기
    }

    /**
     * 플랫폼 스레드와 가상 스레드 비교
     */
    public void compareThreads() throws InterruptedException {
        // 플랫폼 스레드 (기존 방식)
        Thread platformThread = Thread.ofPlatform()
                .name("platform-thread")
                .start(() -> {
                    System.out.println("플랫폼 스레드 - isVirtual: " + Thread.currentThread().isVirtual());
                });

        // 가상 스레드
        Thread virtualThread = Thread.ofVirtual()
                .name("virtual-thread")
                .start(() -> {
                    System.out.println("가상 스레드 - isVirtual: " + Thread.currentThread().isVirtual());
                });

        platformThread.join();
        virtualThread.join();
    }

    /**
     * 가상 스레드 팩토리 사용
     */
    public void virtualThreadFactory() {
        var factory = Thread.ofVirtual()
                .name("worker-", 0) // worker-0, worker-1, worker-2, ...
                .factory();

        Thread t1 = factory.newThread(() -> System.out.println(Thread.currentThread().getName()));
        Thread t2 = factory.newThread(() -> System.out.println(Thread.currentThread().getName()));

        t1.start();
        t2.start();
    }
}
