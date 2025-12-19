package com.autocat.playground.java25features;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * Java 22 - Unnamed Variables and Patterns (이름 없는 변수와 패턴)
 *
 * 사용하지 않는 변수를 밑줄(_)로 표시할 수 있습니다.
 * 코드의 의도를 명확히 하고, 사용하지 않는 변수에 대한 경고를 방지합니다.
 *
 * 사용 가능한 곳:
 * - 지역 변수 선언
 * - 람다 매개변수
 * - catch 블록의 예외 변수
 * - for-each 루프 변수
 * - try-with-resources 변수
 * - 레코드 패턴에서 사용하지 않는 컴포넌트
 */
public class UnnamedVariablesExample {

    record Point(int x, int y) {
    }

    record Person(String name, int age, String email) {
    }

    record Order(String id, Person customer, List<String> items) {
    }

    /**
     * 람다에서 사용하지 않는 매개변수
     */
    public void unusedLambdaParameter() {
        Map<String, Integer> scores = Map.of("Alice", 95, "Bob", 87, "Charlie", 92);

        // 기존 방식 - key를 사용하지 않지만 이름을 지어야 함
        scores.forEach((key, value) -> System.out.println("점수: " + value));

        // Java 22+ - 사용하지 않는 매개변수를 _로 표시
        scores.forEach((_, value) -> System.out.println("점수: " + value));

        // 여러 개의 _ 사용 가능
        Map<String, Map<String, Integer>> nestedMap = Map.of(
                "group1", Map.of("a", 1, "b", 2)
        );
        nestedMap.forEach((_, innerMap) ->
                innerMap.forEach((_, value) -> System.out.println(value))
        );
    }

    /**
     * catch 블록에서 예외 변수 무시
     */
    public int parseWithDefault(String input, int defaultValue) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException _) {
            // 예외 객체를 사용하지 않을 때
            return defaultValue;
        }
    }

    /**
     * try-with-resources에서 리소스 변수 무시
     */
    public void autoCloseableExample() {
        // 리소스를 직접 사용하지 않고 사이드 이펙트만 필요한 경우
        try (var _ = new ScopedResource()) {
            System.out.println("작업 수행 중...");
        }
    }

    static class ScopedResource implements AutoCloseable {
        public ScopedResource() {
            System.out.println("리소스 획득");
        }

        @Override
        public void close() {
            System.out.println("리소스 해제");
        }
    }

    /**
     * for-each에서 반복 횟수만 필요한 경우
     */
    public void iterationWithUnusedVariable() {
        List<String> items = List.of("A", "B", "C", "D", "E");

        // 횟수만 세고 요소는 사용하지 않을 때
        int count = 0;
        for (String _ : items) {
            count++;
        }
        System.out.println("총 " + count + "개의 항목");
    }

    /**
     * 레코드 패턴에서 일부 컴포넌트 무시
     */
    public String getPersonName(Object obj) {
        // name만 필요하고 age, email은 필요 없을 때
        if (obj instanceof Person(String name, int _, String _)) {
            return name;
        }
        return "Unknown";
    }

    /**
     * switch 패턴에서 사용
     */
    public String processOrder(Order order) {
        return switch (order) {
            // customer와 items 정보만 필요, id는 무시
            case Order(String _, Person(String name, int _, String _), List<String> items) ->
                    name + "님의 주문: " + items.size() + "개 상품";
        };
    }

    /**
     * 중첩된 레코드 패턴에서 깊은 수준의 무시
     */
    public String extractCustomerCity(Order order) {
        // 고객 이름만 추출, 나머지는 모두 무시
        return switch (order) {
            case Order(_, Person(var name, _, _), _) -> name;
        };
    }

    /**
     * Queue poll 결과 무시 (사이드 이펙트만 필요)
     */
    public void drainQueue(Queue<String> queue, int count) {
        for (int i = 0; i < count && !queue.isEmpty(); i++) {
            var _ = queue.poll();  // 결과는 필요 없고 제거만 필요
        }
    }

    /**
     * 복합 할당에서 사용
     */
    public void multipleUnnamedVariables() {
        // 두 번째 값만 필요한 경우
        var results = Stream.of("skip", "keep", "skip")
                .toList();

        // 인덱스 기반 접근 대신 패턴으로 분해
        if (results.size() >= 3) {
            // 이렇게는 안됨 - 일반 변수 할당에서는 불가
            // var (_, keep, _) = something;

            // 대신 switch나 if-instanceof에서 레코드 패턴으로
            record Triple<T>(T first, T second, T third) {
            }
            var triple = new Triple<>(results.get(0), results.get(1), results.get(2));
            if (triple instanceof Triple(String _, String keep, String _)) {
                System.out.println("두 번째 값: " + keep);
            }
        }
    }
}
