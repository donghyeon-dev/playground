package com.autocat.playground.java25features;

/**
 * Java 23+ - Primitive Types in Patterns (패턴에서의 기본 타입)
 *
 * 패턴 매칭에서 기본 타입(primitive types)을 직접 사용할 수 있습니다.
 * instanceof와 switch에서 int, long, double 등을 패턴으로 사용 가능합니다.
 *
 * 주요 기능:
 * - 기본 타입 패턴 (int, long, double, float, short, byte, char, boolean)
 * - 기본 타입 상수 패턴
 * - 기본 타입 간 안전한 변환 검사
 *
 * 참고: Java 23에서 Preview로 도입
 */
public class PrimitivePatternsExample {

    /**
     * 기본 타입 instanceof 패턴
     */
    public void primitiveInstanceof() {
        Object value = 42;

        // 기존에는 래퍼 클래스만 가능
        if (value instanceof Integer i) {
            System.out.println("Integer: " + i);
        }

        // Java 23+ : 기본 타입 패턴 사용 가능
        if (value instanceof int i) {
            System.out.println("int: " + i);
        }
    }

    /**
     * switch에서 기본 타입 패턴
     */
    public String classifyNumber(Object obj) {
        return switch (obj) {
            case int i when i < 0 -> "음수 int: " + i;
            case int i when i == 0 -> "영";
            case int i when i > 0 -> "양수 int: " + i;
            case long l -> "long: " + l;
            case double d when Double.isNaN(d) -> "NaN";
            case double d when Double.isInfinite(d) -> "무한대";
            case double d -> "double: " + d;
            case float f -> "float: " + f;
            case short s -> "short: " + s;
            case byte b -> "byte: " + b;
            case char c -> "char: " + c;
            case boolean bool -> "boolean: " + bool;
            default -> "알 수 없는 타입";
        };
    }

    /**
     * 기본 타입 상수 패턴
     * 특정 값과 매칭
     */
    public String describeDay(int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> "월요일";
            case 2 -> "화요일";
            case 3 -> "수요일";
            case 4 -> "목요일";
            case 5 -> "금요일";
            case 6, 7 -> "주말";
            case int other when other < 1 -> "유효하지 않은 날 (음수)";
            case int other -> "유효하지 않은 날: " + other;
        };
    }

    /**
     * 안전한 타입 변환 검사
     * 데이터 손실 없이 변환 가능한지 확인
     */
    public String safeConversion(Object value) {
        return switch (value) {
            // int로 안전하게 변환 가능한 경우만 매칭
            case int i -> "int로 표현 가능: " + i;
            case long l when l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE ->
                    "int 범위 내 long: " + l;
            case long l -> "int 범위 초과 long: " + l;
            case double d when d == (int) d -> "정수로 변환 가능한 double: " + (int) d;
            case double d -> "소수점이 있는 double: " + d;
            default -> "변환 불가";
        };
    }

    /**
     * 레코드 패턴과 기본 타입 결합
     */
    record Point(int x, int y) {
    }

    record Circle(Point center, double radius) {
    }

    public String analyzeShape(Object shape) {
        return switch (shape) {
            case Point(int x, int y) when x == 0 && y == 0 -> "원점";
            case Point(int x, int y) when x == y -> "대각선 위의 점 (" + x + ", " + y + ")";
            case Point(int x, int y) -> "일반 점 (" + x + ", " + y + ")";
            case Circle(Point(int x, int y), double r) when r == 0 -> "반지름이 0인 점";
            case Circle(Point(int x, int y), double r) when r > 100 -> "큰 원 (중심: " + x + ", " + y + ")";
            case Circle(_, double r) -> "원 (반지름: " + r + ")";
            default -> "알 수 없는 도형";
        };
    }

    /**
     * 기본 타입 범위 검사
     */
    public String checkRange(Number num) {
        return switch (num) {
            case Integer i when i >= 0 && i <= 100 -> "0-100 범위 정수";
            case Integer i when i < 0 -> "음수 정수";
            case Integer i -> "100 초과 정수";
            case Double d when d >= 0.0 && d <= 1.0 -> "0.0-1.0 범위 실수 (확률)";
            case Double d when d < 0 -> "음수 실수";
            case Double d -> "1.0 초과 실수";
            case Long l when l > Integer.MAX_VALUE -> "Integer 범위 초과 Long";
            case Long l -> "Integer 범위 내 Long";
            default -> "기타 숫자";
        };
    }

    /**
     * boolean 패턴
     */
    public String describeBoolean(Object value) {
        return switch (value) {
            case true -> "참입니다";
            case false -> "거짓입니다";
            case Boolean b -> "Boolean 객체: " + b;  // null Boolean
            case String s when "true".equalsIgnoreCase(s) -> "문자열 true";
            case String s when "false".equalsIgnoreCase(s) -> "문자열 false";
            default -> "boolean이 아닙니다";
        };
    }

    /**
     * char 패턴
     */
    public String classifyChar(Object ch) {
        return switch (ch) {
            case char c when c >= 'a' && c <= 'z' -> "소문자: " + c;
            case char c when c >= 'A' && c <= 'Z' -> "대문자: " + c;
            case char c when c >= '0' && c <= '9' -> "숫자: " + c;
            case char c when Character.isWhitespace(c) -> "공백 문자";
            case char c -> "특수 문자: " + c;
            default -> "문자가 아닙니다";
        };
    }

    /**
     * 실전 예제: HTTP 상태 코드 분류
     */
    public String classifyHttpStatus(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 301 -> "Moved Permanently";
            case 302 -> "Found";
            case 304 -> "Not Modified";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            case int s when s >= 100 && s < 200 -> "Informational (" + s + ")";
            case int s when s >= 200 && s < 300 -> "Success (" + s + ")";
            case int s when s >= 300 && s < 400 -> "Redirection (" + s + ")";
            case int s when s >= 400 && s < 500 -> "Client Error (" + s + ")";
            case int s when s >= 500 && s < 600 -> "Server Error (" + s + ")";
            case int s -> "Unknown Status (" + s + ")";
        };
    }

    public static void main(String[] args) {
        PrimitivePatternsExample example = new PrimitivePatternsExample();

        System.out.println(example.classifyNumber(42));
        System.out.println(example.classifyNumber(3.14));
        System.out.println(example.describeDay(1));
        System.out.println(example.classifyHttpStatus(200));
        System.out.println(example.classifyHttpStatus(418)); // I'm a teapot
    }
}
