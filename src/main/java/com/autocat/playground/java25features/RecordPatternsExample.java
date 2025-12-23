package com.autocat.playground.java25features;

/**
 * Java 21 - Record Patterns (레코드 패턴)
 *
 * 레코드의 컴포넌트를 직접 분해(destructure)하여 추출할 수 있습니다.
 * 중첩된 레코드도 한 번에 분해 가능합니다.
 *
 * 주요 특징:
 * - 레코드 컴포넌트 자동 추출
 * - 중첩 패턴 지원
 * - instanceof와 switch에서 모두 사용 가능
 * - 타입 추론으로 간결한 코드
 */
public class RecordPatternsExample {

    // 기본 레코드 정의
    record Point(int x, int y) {
    }

    record Line(Point start, Point end) {
    }

    record ColoredPoint(Point point, String color) {
    }

    record Rectangle(Point topLeft, Point bottomRight) {
    }

    // 중첩된 레코드 구조
    record Customer(String name, Address address) {
    }

    record Address(String city, String street, int zipCode) {
    }

    record Order(Customer customer, Product product, int quantity) {
    }

    record Product(String name, double price) {
    }

    /**
     * 기본 레코드 패턴 - instanceof 사용
     */
    public void basicRecordPattern(Object obj) {
        // 기존 방식 (Java 16 이전)
        if (obj instanceof Point) {
            Point p = (Point) obj;
            System.out.println("x: " + p.x() + ", y: " + p.y());
        }

        // Java 16+ 패턴 매칭
        if (obj instanceof Point p) {
            System.out.println("Point: " + p);
        }

        // Java 21 레코드 패턴 - 컴포넌트 직접 추출
        if (obj instanceof Point(int x, int y)) {
            System.out.println("x: " + x + ", y: " + y);
        }
    }

    /**
     * 중첩 레코드 패턴
     */
    public String analyzeColoredPoint(Object obj) {
        // 중첩된 레코드도 한 번에 분해
        if (obj instanceof ColoredPoint(Point(int x, int y), String color)) {
            return String.format("색상 %s의 점이 (%d, %d)에 위치", color, x, y);
        }
        return "ColoredPoint가 아닙니다";
    }

    /**
     * switch에서 레코드 패턴 사용
     */
    public double calculateDistance(Object obj) {
        return switch (obj) {
            case Point(int x, int y) -> Math.sqrt(x * x + y * y);
            case Line(Point(int x1, int y1), Point(int x2, int y2)) ->
                    Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            case Rectangle(Point(int x1, int y1), Point(int x2, int y2)) ->
                    Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)); // 대각선 거리
            default -> 0;
        };
    }

    /**
     * var를 사용한 타입 추론
     */
    public String processLine(Object obj) {
        // var를 사용하면 타입을 명시적으로 쓰지 않아도 됨
        if (obj instanceof Line(var start, var end)) {
            return "선분: " + start + " -> " + end;
        }
        return "Line이 아닙니다";
    }

    /**
     * 복잡한 중첩 패턴
     */
    public String processOrder(Order order) {
        // 깊게 중첩된 레코드 분해
        if (order instanceof Order(
                Customer(var name, Address(var city, _, _)),
                Product(var productName, var price),
                var qty
        )) {
            double total = price * qty;
            return String.format("%s님 (%s 거주) - %s %d개 주문, 총액: %.2f원",
                    name, city, productName, qty, total);
        }
        return "주문 정보 처리 실패";
    }

    /**
     * when 절과 함께 사용
     */
    public String classifyPoint(Object obj) {
        return switch (obj) {
            case Point(int x, int y) when x == 0 && y == 0 -> "원점";
            case Point(int x, int y) when x == 0 -> "Y축 위의 점";
            case Point(int x, int y) when y == 0 -> "X축 위의 점";
            case Point(int x, int y) when x > 0 && y > 0 -> "제1사분면";
            case Point(int x, int y) when x < 0 && y > 0 -> "제2사분면";
            case Point(int x, int y) when x < 0 && y < 0 -> "제3사분면";
            case Point(int x, int y) -> "제4사분면";
            default -> "Point가 아닙니다";
        };
    }
}
