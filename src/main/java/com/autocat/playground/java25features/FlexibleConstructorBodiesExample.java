package com.autocat.playground.java25features;

/**
 * Java 22/24 - Statements before super() / Flexible Constructor Bodies
 * (super() 이전 문장 / 유연한 생성자 본문)
 *
 * 생성자에서 super() 또는 this() 호출 전에 문장을 실행할 수 있습니다.
 * 단, 인스턴스 멤버에 접근하거나 수정하는 것은 여전히 불가능합니다.
 *
 * 주요 사용 사례:
 * - 부모 생성자에 전달할 인수 검증
 * - 복잡한 인수 계산
 * - 방어적 복사본 생성
 * - 로깅 및 디버깅
 *
 * Java 22에서 Preview로 도입, Java 24에서 기능 확장
 */
public class FlexibleConstructorBodiesExample {

    /**
     * 기본 예제: 인수 검증
     */
    static class PositiveNumber {
        private final int value;

        public PositiveNumber(int value) {
            // Java 22+ : super() 호출 전에 검증 가능
            if (value <= 0) {
                throw new IllegalArgumentException("값은 양수여야 합니다: " + value);
            }
            // 검증 후 super() 호출 (Object의 생성자)
            super();
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 부모 클래스에 전달할 값 전처리
     */
    static class Animal {
        private final String name;

        public Animal(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    static class Dog extends Animal {
        private final String breed;

        public Dog(String rawName, String breed) {
            // super() 호출 전에 이름 정규화
            String normalizedName = rawName.trim().toLowerCase();
            normalizedName = Character.toUpperCase(normalizedName.charAt(0))
                    + normalizedName.substring(1);

            // 정규화된 이름으로 부모 생성자 호출
            super(normalizedName);
            this.breed = breed;
        }

        public String getBreed() {
            return breed;
        }
    }

    /**
     * 복잡한 인수 계산
     */
    static class Rectangle {
        private final double width;
        private final double height;

        public Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public double getArea() {
            return width * height;
        }
    }

    static class Square extends Rectangle {
        public Square(double side) {
            // super() 호출 전에 값 검증 및 계산
            if (side <= 0) {
                throw new IllegalArgumentException("변의 길이는 양수여야 합니다");
            }
            double validatedSide = Math.abs(side);
            super(validatedSide, validatedSide);
        }
    }

    /**
     * 방어적 복사
     */
    static class ImmutablePerson {
        private final String name;
        private final byte[] data;

        public ImmutablePerson(String name, byte[] data) {
            // super() 호출 전에 방어적 복사 수행
            byte[] dataCopy = data.clone();

            // null 체크도 가능
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("이름은 비어있을 수 없습니다");
            }

            super();
            this.name = name;
            this.data = dataCopy;
        }

        public byte[] getData() {
            return data.clone();  // 반환 시에도 방어적 복사
        }
    }

    /**
     * 로깅 예제
     */
    static class LoggedOperation {
        private final String operationName;

        public LoggedOperation(String operationName) {
            // 생성자 호출 로깅
            System.out.println("[LOG] Creating operation: " + operationName);

            super();
            this.operationName = operationName;

            System.out.println("[LOG] Operation created successfully");
        }
    }

    /**
     * 팩토리 메서드 패턴과 함께 사용
     */
    static class DatabaseConnection {
        private final String url;
        private final int maxRetries;

        public DatabaseConnection(String url, int maxRetries) {
            // 연결 문자열 파싱 및 검증
            if (!url.startsWith("jdbc:")) {
                throw new IllegalArgumentException("유효하지 않은 JDBC URL: " + url);
            }

            if (maxRetries < 0 || maxRetries > 10) {
                throw new IllegalArgumentException("재시도 횟수는 0-10 사이여야 합니다");
            }

            // URL에서 프로토콜 추출하여 로깅
            String protocol = url.substring(5, url.indexOf(":", 5));
            System.out.println("데이터베이스 프로토콜: " + protocol);

            super();
            this.url = url;
            this.maxRetries = maxRetries;
        }
    }

    /**
     * 여러 생성자 체이닝 (this() 사용)
     */
    static class Configuration {
        private final String name;
        private final int timeout;
        private final boolean debug;

        public Configuration(String name) {
            // this() 호출 전에도 문장 사용 가능
            String validatedName = name != null ? name.trim() : "default";
            this(validatedName, 30, false);
        }

        public Configuration(String name, int timeout) {
            if (timeout <= 0) {
                timeout = 30;  // 기본값 적용
            }
            this(name, timeout, false);
        }

        public Configuration(String name, int timeout, boolean debug) {
            super();
            this.name = name;
            this.timeout = timeout;
            this.debug = debug;
        }

        @Override
        public String toString() {
            return "Configuration{name='" + name + "', timeout=" + timeout + ", debug=" + debug + "}";
        }
    }

    public static void main(String[] args) {
        // PositiveNumber 테스트
        PositiveNumber num = new PositiveNumber(42);
        System.out.println("PositiveNumber: " + num.getValue());

        // Dog 테스트
        Dog dog = new Dog("  buddy  ", "Golden Retriever");
        System.out.println("Dog: " + dog.getName() + " (" + dog.getBreed() + ")");

        // Square 테스트
        Square square = new Square(5);
        System.out.println("Square area: " + square.getArea());

        // Configuration 테스트
        Configuration config = new Configuration("  myApp  ");
        System.out.println(config);
    }
}
