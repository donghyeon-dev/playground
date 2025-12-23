package com.autocat.playground.springboot4features.flyway.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Java 기반 마이그레이션 예시
 *
 * SQL로 표현하기 어려운 복잡한 데이터 마이그레이션이나
 * 프로그래밍 로직이 필요한 경우 Java 마이그레이션을 사용합니다.
 *
 * 클래스명 규칙: V{버전}__{설명}
 * - V5__Insert_initial_data
 * - V5_1__Fix_data_issue
 *
 * 주의사항:
 * - 반드시 BaseJavaMigration을 상속해야 합니다.
 * - migrate() 메서드를 구현해야 합니다.
 * - 트랜잭션 내에서 실행됩니다.
 */
public class V5__Insert_initial_data extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        // 초기 관리자 사용자 생성
        insertAdminUser(context);

        // 초기 카테고리/상품 데이터 생성
        insertSampleProducts(context);

        System.out.println("✅ V5: 초기 데이터 삽입 완료");
    }

    private void insertAdminUser(Context context) throws Exception {
        String sql = """
            INSERT INTO users (username, email, password_hash, full_name, status, created_at, email_verified)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = context.getConnection().prepareStatement(sql)) {
            // 관리자 계정
            stmt.setString(1, "admin");
            stmt.setString(2, "admin@example.com");
            stmt.setString(3, "$2a$10$dummyhashvalue"); // 실제로는 BCrypt 해시
            stmt.setString(4, "시스템 관리자");
            stmt.setString(5, "ACTIVE");
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(7, true);
            stmt.executeUpdate();

            System.out.println("  - 관리자 계정 생성: admin@example.com");
        }
    }

    private void insertSampleProducts(Context context) throws Exception {
        String sql = """
            INSERT INTO products (name, description, price, stock_quantity, category, is_active, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        Object[][] products = {
                {"프리미엄 노트북", "고성능 비즈니스 노트북", 1500000.00, 50, "전자기기", true},
                {"무선 마우스", "인체공학적 무선 마우스", 45000.00, 200, "주변기기", true},
                {"기계식 키보드", "청축 기계식 키보드", 120000.00, 100, "주변기기", true},
                {"27인치 모니터", "QHD 게이밍 모니터", 450000.00, 30, "전자기기", true},
                {"USB-C 허브", "7-in-1 USB-C 멀티 허브", 55000.00, 150, "주변기기", true},
        };

        try (PreparedStatement stmt = context.getConnection().prepareStatement(sql)) {
            for (Object[] product : products) {
                stmt.setString(1, (String) product[0]);
                stmt.setString(2, (String) product[1]);
                stmt.setDouble(3, (Double) product[2]);
                stmt.setInt(4, (Integer) product[3]);
                stmt.setString(5, (String) product[4]);
                stmt.setBoolean(6, (Boolean) product[5]);
                stmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                stmt.addBatch();
            }
            stmt.executeBatch();

            System.out.println("  - 샘플 상품 " + products.length + "개 생성");
        }
    }

    /**
     * 마이그레이션 체크섬 (선택사항)
     * 마이그레이션 내용이 변경되었는지 확인하는 데 사용됩니다.
     */
    @Override
    public Integer getChecksum() {
        return 1; // 내용 변경 시 이 값도 변경해야 합니다
    }
}
