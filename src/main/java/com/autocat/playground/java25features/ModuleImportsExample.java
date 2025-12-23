package com.autocat.playground.java25features;

// Java 23+ : Module Import Declarations (모듈 임포트 선언)
// 전체 모듈을 한 번에 임포트할 수 있습니다.
// import module java.base;  // 모든 java.base 패키지 임포트
// import module java.sql;   // 모든 java.sql 패키지 임포트

// 이 기능은 컴파일러 레벨에서 지원되며, 현재 Preview 상태입니다.
// 아래는 개념적 설명입니다.

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Java 23+ - Module Import Declarations & Implicitly Declared Classes
 * (모듈 임포트 선언 및 암시적 선언 클래스)
 *
 * 1. Module Import Declarations
 *    - 전체 모듈의 모든 패키지를 한 번에 임포트
 *    - import module java.base; 형태로 사용
 *
 * 2. Implicitly Declared Classes (암시적 선언 클래스)
 *    - class 선언 없이 바로 코드 작성 가능
 *    - 작은 프로그램이나 스크립트에 유용
 *    - main 메서드 간소화
 *
 * 참고: Java 23에서 Preview로 도입
 */
public class ModuleImportsExample {

    /**
     * Module Import 개념 설명
     *
     * 기존 방식:
     * import java.util.List;
     * import java.util.Map;
     * import java.util.Set;
     * import java.util.concurrent.ExecutorService;
     * import java.util.concurrent.Future;
     * import java.time.LocalDate;
     * import java.time.LocalTime;
     * import java.time.Duration;
     *
     * Java 23+ 모듈 임포트 방식:
     * import module java.base;  // 위의 모든 것을 한 번에!
     *
     * 장점:
     * - 코드 간결화
     * - 탐색적 프로그래밍에 유용
     * - 학습 목적의 코드에 적합
     *
     * 주의:
     * - 이름 충돌 가능성
     * - IDE 자동완성이 많은 옵션 제시
     */
    public void moduleImportConcept() {
        // java.base 모듈에 포함된 패키지들 (일부):
        // - java.lang (자동 임포트)
        // - java.util
        // - java.io
        // - java.time
        // - java.util.concurrent
        // - java.util.function
        // - java.util.stream

        List<String> list = new ArrayList<>();  // java.util
        LocalDate today = LocalDate.now();       // java.time
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();  // java.util.concurrent

        System.out.println("Module import로 이 모든 클래스를 import module java.base; 하나로!");
    }

    /**
     * Implicitly Declared Classes (암시적 선언 클래스)
     *
     * 기존 Java:
     * ```java
     * public class HelloWorld {
     *     public static void main(String[] args) {
     *         System.out.println("Hello, World!");
     *     }
     * }
     * ```
     *
     * Java 23+ (암시적 클래스):
     * ```java
     * void main() {
     *     println("Hello, World!");
     * }
     * ```
     *
     * 또는 더 간단하게:
     * ```java
     * void main() {
     *     System.out.println("Hello, World!");
     * }
     * ```
     */

    /**
     * 새로운 main 메서드 형태들 (Java 21+)
     *
     * 1. 전통적인 형태 (여전히 지원)
     *    public static void main(String[] args)
     *
     * 2. String[] args 생략 가능
     *    public static void main()
     *
     * 3. static 생략 가능 (인스턴스 main)
     *    public void main()
     *
     * 4. public 생략 가능
     *    void main()
     */

    /**
     * 암시적 클래스에서 자동 임포트되는 메서드들
     *
     * - println(Object) : System.out.println과 동일
     * - print(Object)   : System.out.print와 동일
     * - readln()        : 콘솔에서 한 줄 읽기
     */

    // 파일: SimpleScript.java (암시적 클래스 예시)
    // 아래는 별도 파일로 작성해야 하는 예시입니다.
    /*
    // 클래스 선언 없이 바로 코드 작성!
    void main() {
        var name = "Java 23";

        println("Hello, " + name + "!");
        println("오늘 날짜: " + java.time.LocalDate.now());

        // 간단한 계산
        var numbers = java.util.List.of(1, 2, 3, 4, 5);
        var sum = numbers.stream().mapToInt(Integer::intValue).sum();
        println("합계: " + sum);
    }
    */

    /**
     * 실전 활용 예시: 빠른 프로토타이핑
     */
    public void quickPrototyping() {
        // 기존에는 이렇게 많은 import가 필요했지만...
        // import module java.base; 한 줄이면 모든 기본 클래스 사용 가능

        // 컬렉션 작업
        var names = new ArrayList<>(List.of("Alice", "Bob", "Charlie"));
        var nameMap = new HashMap<String, Integer>();
        var nameSet = new HashSet<String>();

        // 시간 작업
        var now = LocalDateTime.now();
        var tomorrow = now.plusDays(1);
        var duration = Duration.between(now, tomorrow);

        // 동시성 작업
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = names.stream()
                    .map(name -> executor.submit(() -> "Hello, " + name))
                    .toList();
        }

        System.out.println("빠른 프로토타이핑 완료!");
    }

    public static void main(String[] args) {
        ModuleImportsExample example = new ModuleImportsExample();
        example.moduleImportConcept();
        example.quickPrototyping();
    }
}
