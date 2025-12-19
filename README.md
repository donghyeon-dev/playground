# Java 25 & Spring Boot 4.0 Playground

Java 25와 Spring Boot 4.0의 새로운 기능들을 학습하고 실험하기 위한 프로젝트입니다.

## 요구 사항

- **Java 25** (Preview 기능 포함)
- **Gradle 8.12+**
- **Spring Boot 4.0.0**

## 프로젝트 구조

```
src/main/java/com/autocat/playground/
├── java25features/          # Java 17→25 새로운 기능 예시
│   ├── VirtualThreadsExample.java
│   ├── PatternMatchingSwitchExample.java
│   ├── RecordPatternsExample.java
│   ├── SequencedCollectionsExample.java
│   ├── UnnamedVariablesExample.java
│   ├── FlexibleConstructorBodiesExample.java
│   ├── StreamGatherersExample.java
│   ├── ScopedValuesExample.java
│   ├── StructuredConcurrencyExample.java
│   ├── PrimitivePatternsExample.java
│   └── ModuleImportsExample.java
│
├── springboot4features/     # Spring Boot 4.0 새로운 기능 예시
│   ├── VirtualThreadsAutoConfiguration.java
│   ├── HttpInterfaceClientExample.java
│   ├── RestClientExample.java
│   ├── ObservabilityExample.java
│   ├── ProblemDetailsExample.java
│   ├── ConfigurationPropertiesExample.java
│   ├── ActuatorEnhancementsExample.java
│   ├── NativeImageSupportExample.java
│   └── SecurityEnhancementsExample.java
│
├── feign_with_decoder/      # Feign Client with Custom Decoder
└── lambda/                  # Java Lambda 예시
```

---

## Java 17 → 25 주요 기능

### 1. Virtual Threads (가상 스레드) - Java 21
```java
// 간단한 가상 스레드 생성
Thread.startVirtualThread(() -> {
    System.out.println("Virtual Thread 실행!");
});

// ExecutorService 사용
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    IntStream.range(0, 10_000).forEach(i ->
        executor.submit(() -> processTask(i))
    );
}
```

### 2. Pattern Matching for switch - Java 21
```java
String describe(Object obj) {
    return switch (obj) {
        case Integer i when i > 0 -> "양수 정수: " + i;
        case Integer i when i < 0 -> "음수 정수: " + i;
        case Integer i -> "영";
        case String s -> "문자열: " + s;
        case null -> "null";
        default -> "알 수 없음";
    };
}
```

### 3. Record Patterns (레코드 패턴) - Java 21
```java
record Point(int x, int y) {}
record Line(Point start, Point end) {}

// 중첩된 레코드 분해
if (obj instanceof Line(Point(int x1, int y1), Point(int x2, int y2))) {
    double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
}
```

### 4. Sequenced Collections - Java 21
```java
List<String> list = new ArrayList<>(List.of("A", "B", "C"));

// 첫 번째/마지막 요소 접근
String first = list.getFirst();  // "A"
String last = list.getLast();    // "C"

// 역순 뷰
SequencedCollection<String> reversed = list.reversed();
```

### 5. Unnamed Variables and Patterns - Java 22
```java
// 사용하지 않는 변수를 _로 표시
map.forEach((_, value) -> System.out.println(value));

// catch 블록에서 예외 무시
try {
    parse(input);
} catch (NumberFormatException _) {
    return defaultValue;
}

// 레코드 패턴에서 일부 무시
if (person instanceof Person(String name, int _, String _)) {
    return name;
}
```

### 6. Stream Gatherers - Java 24/25
```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

// 고정 크기 윈도우
var windows = numbers.stream()
    .gather(Gatherers.windowFixed(3))
    .toList();  // [[1,2,3], [4,5,6], [7,8,9]]

// 슬라이딩 윈도우
var sliding = numbers.stream()
    .gather(Gatherers.windowSliding(3))
    .toList();  // [[1,2,3], [2,3,4], [3,4,5], ...]

// 병렬 매핑
var results = urls.stream()
    .gather(Gatherers.mapConcurrent(4, this::fetchUrl))
    .toList();
```

### 7. Flexible Constructor Bodies - Java 22/24
```java
class PositiveNumber {
    private final int value;

    public PositiveNumber(int value) {
        // super() 호출 전에 검증 가능!
        if (value <= 0) {
            throw new IllegalArgumentException("양수여야 합니다");
        }
        super();
        this.value = value;
    }
}
```

### 8. Structured Concurrency - Java 21+
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var userTask = scope.fork(() -> fetchUser(userId));
    var orderTask = scope.fork(() -> fetchOrder(orderId));

    scope.join();
    scope.throwIfFailed();

    return new Result(userTask.get(), orderTask.get());
}
```

### 9. Scoped Values - Java 21+
```java
private static final ScopedValue<String> USER = ScopedValue.newInstance();

ScopedValue.runWhere(USER, "admin", () -> {
    // 이 범위 내에서 USER.get()으로 값 접근 가능
    processRequest();
});
```

---

## Spring Boot 4.0 주요 기능

### 1. Virtual Threads 자동 구성
```properties
# application.properties
spring.threads.virtual.enabled=true
server.tomcat.threads.virtual=true
```

### 2. HTTP Interface Client (@HttpExchange)
```java
@HttpExchange(url = "/api/users", accept = "application/json")
public interface UserClient {

    @GetExchange
    List<User> getAllUsers();

    @GetExchange("/{id}")
    User getUserById(@PathVariable Long id);

    @PostExchange
    User createUser(@RequestBody CreateUserRequest request);

    @DeleteExchange("/{id}")
    void deleteUser(@PathVariable Long id);
}
```

### 3. RestClient 개선
```java
RestClient restClient = RestClient.builder()
    .baseUrl("https://api.example.com")
    .build();

User user = restClient.get()
    .uri("/users/{id}", 1)
    .retrieve()
    .body(User.class);
```

### 4. Problem Details (RFC 9457)
```java
ProblemDetail problem = ProblemDetail.forStatusAndDetail(
    HttpStatus.NOT_FOUND,
    "User not found"
);
problem.setType(URI.create("https://api.example.com/errors/not-found"));
problem.setTitle("Resource Not Found");
problem.setProperty("userId", userId);
```

### 5. Observability (관찰성)
```java
@Observed(name = "user.fetch", contextualName = "fetching-user")
public User fetchUser(Long userId) {
    return userRepository.findById(userId);
}
```

### 6. Configuration Properties with Records
```java
@ConfigurationProperties(prefix = "app")
public record AppConfig(
    @NotBlank String name,
    @DefaultValue("8080") int port,
    @DefaultValue("30s") Duration timeout,
    List<Endpoint> endpoints
) {}
```

### 7. GraalVM Native Image 지원
```bash
# 네이티브 이미지 빌드
./gradlew nativeCompile

# 네이티브 테스트
./gradlew nativeTest

# Docker 이미지 빌드
./gradlew bootBuildImage
```

---

## 실행 방법

### 개발 모드
```bash
./gradlew bootRun
```

### 테스트
```bash
./gradlew test
```

### 빌드
```bash
./gradlew build
```

### 네이티브 이미지 빌드
```bash
./gradlew nativeCompile
```

---

## 설정

### application.properties 주요 설정
```properties
# Virtual Threads 활성화
spring.threads.virtual.enabled=true

# Actuator 엔드포인트
management.endpoints.web.exposure.include=health,info,metrics

# Observability
management.tracing.enabled=true
management.tracing.sampling.probability=1.0

# Problem Details
spring.mvc.problemdetails.enabled=true
```

---

## 참고 자료

### Java
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [JEP 440: Record Patterns](https://openjdk.org/jeps/440)
- [JEP 431: Sequenced Collections](https://openjdk.org/jeps/431)
- [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456)
- [JEP 473: Stream Gatherers](https://openjdk.org/jeps/473)
- [JEP 462: Structured Concurrency](https://openjdk.org/jeps/462)
- [JEP 464: Scoped Values](https://openjdk.org/jeps/464)

### Spring Boot
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki)
- [Spring Framework 7.0 Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring Security 7.0 Reference](https://docs.spring.io/spring-security/reference/)

---

## 라이선스

MIT License
