package com.autocat.playground.java25features;

/**
 * Java 21 - Pattern Matching for switch (switch 패턴 매칭)
 *
 * switch 문에서 타입 패턴과 가드(when)를 사용할 수 있습니다.
 * instanceof 검사와 캐스팅을 하나의 표현식으로 결합합니다.
 *
 * 주요 특징:
 * - 타입 패턴으로 instanceof + 캐스팅 대체
 * - when 절로 추가 조건 지정 (가드)
 * - null 케이스 직접 처리 가능
 * - 완전성 검사 (sealed 클래스와 함께 사용 시)
 */
public class PatternMatchingSwitchExample {

    // sealed 클래스 정의 (Java 17)
    sealed interface Shape permits Circle, Rectangle, Triangle {
    }

    record Circle(double radius) implements Shape {
    }

    record Rectangle(double width, double height) implements Shape {
    }

    record Triangle(double base, double height) implements Shape {
    }

    /**
     * 기본 타입 패턴 매칭
     */
    public String describeObject(Object obj) {
        return switch (obj) {
            case Integer i -> "정수: " + i;
            case Long l -> "Long 정수: " + l;
            case Double d -> "실수: " + d;
            case String s -> "문자열 (길이: " + s.length() + "): " + s;
            case int[] arr -> "int 배열 (크기: " + arr.length + ")";
            case null -> "null 값";
            default -> "알 수 없는 타입: " + obj.getClass().getName();
        };
    }

    /**
     * when 절 (가드) 사용
     * - 패턴 매칭 후 추가 조건 검사
     */
    public String classifyNumber(Number number) {
        return switch (number) {
            case Integer i when i < 0 -> "음수 정수: " + i;
            case Integer i when i == 0 -> "영";
            case Integer i when i > 0 && i <= 100 -> "1~100 사이 정수: " + i;
            case Integer i -> "100 초과 정수: " + i;
            case Double d when d.isNaN() -> "NaN";
            case Double d when d.isInfinite() -> "무한대";
            case Double d when d < 0 -> "음수 실수: " + d;
            case Double d -> "양수 실수: " + d;
            case null -> "null";
            default -> "기타 숫자 타입: " + number;
        };
    }

    /**
     * sealed 클래스와 함께 사용 (완전성 보장)
     */
    public double calculateArea(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t -> 0.5 * t.base() * t.height();
            // sealed 클래스이므로 default 불필요 (완전성 보장)
        };
    }

    /**
     * null 안전 처리
     */
    public String safeToString(Object obj) {
        return switch (obj) {
            case null -> "값이 없습니다";
            case String s when s.isBlank() -> "빈 문자열";
            case String s -> s;
            default -> obj.toString();
        };
    }

    /**
     * 중첩된 패턴 매칭
     */
    public String processShape(Shape shape) {
        return switch (shape) {
            case Circle c when c.radius() > 10 -> "큰 원 (반지름: " + c.radius() + ")";
            case Circle c when c.radius() > 0 -> "작은 원 (반지름: " + c.radius() + ")";
            case Circle c -> "유효하지 않은 원";
            case Rectangle r when r.width() == r.height() -> "정사각형 (" + r.width() + "x" + r.height() + ")";
            case Rectangle r -> "직사각형 (" + r.width() + "x" + r.height() + ")";
            case Triangle t -> "삼각형 (밑변: " + t.base() + ", 높이: " + t.height() + ")";
        };
    }
}
