package com.autocat.playground.java25features;

import java.util.List;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * Java 24/25 - Stream Gatherers (스트림 수집기)
 *
 * Stream API에 새로운 중간 연산인 gather()가 추가되었습니다.
 * 기존 map, filter, flatMap으로 구현하기 어려운 복잡한 변환을 가능하게 합니다.
 *
 * 주요 내장 Gatherers:
 * - fold: 누적 연산 (reduce와 유사하지만 중간 연산)
 * - scan: 누적 과정의 모든 중간값 출력
 * - windowFixed: 고정 크기 윈도우
 * - windowSliding: 슬라이딩 윈도우
 * - mapConcurrent: 병렬 매핑
 *
 * 참고: Java 24에서 Preview로 도입, Java 25에서 정식 기능으로 확정 예정
 */
public class StreamGatherersExample {

    /**
     * windowFixed - 고정 크기 윈도우
     * 요소들을 고정된 크기의 그룹으로 분할
     */
    public void windowFixedExample() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 3개씩 묶어서 처리
        var windows = numbers.stream()
                .gather(Gatherers.windowFixed(3))
                .toList();

        System.out.println("Fixed windows (size 3):");
        windows.forEach(window -> System.out.println("  " + window));
        // [1, 2, 3]
        // [4, 5, 6]
        // [7, 8, 9]
        // [10]  <- 마지막 윈도우는 3개 미만일 수 있음
    }

    /**
     * windowSliding - 슬라이딩 윈도우
     * 연속된 요소들의 겹치는 윈도우 생성
     */
    public void windowSlidingExample() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // 3개씩 슬라이딩 윈도우
        var windows = numbers.stream()
                .gather(Gatherers.windowSliding(3))
                .toList();

        System.out.println("Sliding windows (size 3):");
        windows.forEach(window -> System.out.println("  " + window));
        // [1, 2, 3]
        // [2, 3, 4]
        // [3, 4, 5]
    }

    /**
     * 슬라이딩 윈도우 활용 - 이동 평균 계산
     */
    public void movingAverageExample() {
        List<Double> prices = List.of(100.0, 102.0, 104.0, 103.0, 105.0, 107.0, 106.0);

        // 3일 이동 평균
        var movingAverages = prices.stream()
                .gather(Gatherers.windowSliding(3))
                .map(window -> window.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0))
                .toList();

        System.out.println("3일 이동 평균: " + movingAverages);
        // [102.0, 103.0, 104.0, 105.0, 106.0]
    }

    /**
     * fold - 누적 연산 (중간 연산 버전)
     * reduce와 비슷하지만 단일 결과가 아닌 스트림의 일부로 동작
     */
    public void foldExample() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // fold로 합계 계산 (초기값, 누적 함수)
        var sum = numbers.stream()
                .gather(Gatherers.fold(() -> 0, Integer::sum))
                .findFirst()
                .orElse(0);

        System.out.println("합계: " + sum);  // 15

        // 문자열 연결
        List<String> words = List.of("Hello", "World", "Java", "25");
        var concatenated = words.stream()
                .gather(Gatherers.fold(() -> "", (acc, word) -> acc.isEmpty() ? word : acc + " " + word))
                .findFirst()
                .orElse("");

        System.out.println("연결된 문자열: " + concatenated);  // Hello World Java 25
    }

    /**
     * scan - 누적 과정의 모든 중간 상태 출력
     * fold와 비슷하지만 모든 중간 결과를 스트림으로 출력
     */
    public void scanExample() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // scan으로 누적 합계의 모든 중간 상태
        var runningSums = numbers.stream()
                .gather(Gatherers.scan(() -> 0, Integer::sum))
                .toList();

        System.out.println("누적 합계 과정: " + runningSums);
        // [1, 3, 6, 10, 15]  (1, 1+2, 1+2+3, 1+2+3+4, 1+2+3+4+5)

        // 팩토리얼 계산 과정
        var factorials = Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.scan(() -> 1, (acc, n) -> acc * n))
                .toList();

        System.out.println("팩토리얼 과정: " + factorials);
        // [1, 2, 6, 24, 120]
    }

    /**
     * mapConcurrent - 병렬 매핑
     * 지정된 동시성 수준으로 병렬 처리
     */
    public void mapConcurrentExample() {
        List<String> urls = List.of(
                "https://api.example.com/user/1",
                "https://api.example.com/user/2",
                "https://api.example.com/user/3",
                "https://api.example.com/user/4"
        );

        // 최대 2개의 동시 요청으로 처리
        var results = urls.stream()
                .gather(Gatherers.mapConcurrent(2, url -> fetchData(url)))
                .toList();

        System.out.println("병렬 처리 결과: " + results);
    }

    // 시뮬레이션 메서드
    private String fetchData(String url) {
        try {
            Thread.sleep(100);  // 네트워크 지연 시뮬레이션
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Response from " + url;
    }

    /**
     * Gatherers 조합 사용
     */
    public void combinedGatherersExample() {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 슬라이딩 윈도우로 이동 합계 계산 후, 5보다 큰 것만 필터링
        var result = numbers.stream()
                .gather(Gatherers.windowSliding(3))
                .map(window -> window.stream().mapToInt(Integer::intValue).sum())
                .filter(sum -> sum > 15)
                .toList();

        System.out.println("합계가 15 초과인 윈도우 합계: " + result);
        // 3-요소 윈도우 합계: 6, 9, 12, 15, 18, 21, 24, 27
        // 15 초과: [18, 21, 24, 27]
    }

    /**
     * 실전 예제: 로그 분석
     */
    public void logAnalysisExample() {
        List<LogEntry> logs = List.of(
                new LogEntry("2024-01-01 10:00", "INFO", "App started"),
                new LogEntry("2024-01-01 10:01", "ERROR", "Connection failed"),
                new LogEntry("2024-01-01 10:02", "ERROR", "Retry failed"),
                new LogEntry("2024-01-01 10:03", "INFO", "Connection restored"),
                new LogEntry("2024-01-01 10:04", "WARN", "High memory usage")
        );

        // 연속된 ERROR 로그 감지
        var errorBursts = logs.stream()
                .gather(Gatherers.windowSliding(2))
                .filter(window -> window.stream()
                        .allMatch(log -> "ERROR".equals(log.level())))
                .toList();

        System.out.println("연속 에러 발생:");
        errorBursts.forEach(burst -> System.out.println("  " + burst));
    }

    record LogEntry(String timestamp, String level, String message) {
    }
}
