package com.autocat.playground.springboot4features.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Spring Boot 4.0 - Flyway 설정 예시
 *
 * Spring Boot 4.0에서는 Flyway 자동 구성이 더욱 강화되었습니다.
 * 대부분의 경우 application.properties 설정만으로 충분하지만,
 * 고급 설정이 필요한 경우 Java Config를 사용할 수 있습니다.
 *
 * application.properties 기본 설정:
 * ```
 * # Flyway 활성화 (기본값: true)
 * spring.flyway.enabled=true
 *
 * # 마이그레이션 파일 위치
 * spring.flyway.locations=classpath:db/migration
 *
 * # 기준 버전 (기존 DB에 Flyway 적용 시)
 * spring.flyway.baseline-on-migrate=true
 * spring.flyway.baseline-version=0
 *
 * # 검증 모드
 * spring.flyway.validate-on-migrate=true
 *
 * # 스키마 생성
 * spring.flyway.create-schemas=true
 * spring.flyway.default-schema=public
 * ```
 */
@Configuration
public class FlywayConfiguration {

    /**
     * Flyway 설정 커스터마이저
     * Spring Boot의 자동 구성을 커스터마이징합니다.
     */
    @Bean
    public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
        return configuration -> configuration
                // 마이그레이션 파일 위치 (여러 위치 지정 가능)
                .locations(
                        "classpath:db/migration",
                        "classpath:db/seed"  // 시드 데이터용
                )

                // 플레이스홀더 설정 (SQL 내 변수 치환)
                .placeholders(Map.of(
                        "schema_name", "public",
                        "table_prefix", "app_"
                ))
                .placeholderReplacement(true)

                // 검증 설정
                .validateOnMigrate(true)
                .cleanDisabled(true)  // clean 명령 비활성화 (프로덕션 안전)

                // 실행 설정
                .outOfOrder(false)    // 순서대로만 실행
                .mixed(false)         // 트랜잭션/비트랜잭션 혼합 비허용

                // 콜백 등록
                .callbacks(new FlywayLoggingCallback());
    }

    /**
     * 마이그레이션 이벤트 콜백
     * 마이그레이션 전/후에 로깅이나 알림 등을 수행합니다.
     */
    public static class FlywayLoggingCallback implements Callback {

        @Override
        public boolean supports(Event event, Context context) {
            // 관심 있는 이벤트만 처리
            return event == Event.BEFORE_MIGRATE
                    || event == Event.AFTER_MIGRATE
                    || event == Event.AFTER_EACH_MIGRATE
                    || event == Event.AFTER_MIGRATE_ERROR;
        }

        @Override
        public boolean canHandleInTransaction(Event event, Context context) {
            return true;
        }

        @Override
        public void handle(Event event, Context context) {
            switch (event) {
                case BEFORE_MIGRATE -> {
                    System.out.println("=".repeat(60));
                    System.out.println("🚀 Flyway 마이그레이션 시작");
                    System.out.println("현재 버전: " + getCurrentVersion(context));
                    System.out.println("=".repeat(60));
                }
                case AFTER_EACH_MIGRATE -> {
                    var migrationInfo = context.getMigrationInfo();
                    if (migrationInfo != null) {
                        System.out.println("✅ 마이그레이션 완료: " + migrationInfo.getVersion()
                                + " - " + migrationInfo.getDescription());
                    }
                }
                case AFTER_MIGRATE -> {
                    System.out.println("=".repeat(60));
                    System.out.println("🎉 모든 마이그레이션이 성공적으로 완료되었습니다!");
                    System.out.println("최종 버전: " + getCurrentVersion(context));
                    System.out.println("=".repeat(60));
                }
                case AFTER_MIGRATE_ERROR -> {
                    System.err.println("❌ 마이그레이션 실패!");
                    var migrationInfo = context.getMigrationInfo();
                    if (migrationInfo != null) {
                        System.err.println("실패한 마이그레이션: " + migrationInfo.getVersion()
                                + " - " + migrationInfo.getDescription());
                    }
                }
                default -> {
                }
            }
        }

        private String getCurrentVersion(Context context) {
            try {
                var info = context.getConfiguration().getDataSource();
                // 실제로는 Flyway 인스턴스에서 현재 버전 조회
                return "확인 필요";
            } catch (Exception e) {
                return "알 수 없음";
            }
        }

        @Override
        public String getCallbackName() {
            return "FlywayLoggingCallback";
        }
    }

    /**
     * 프로그래매틱 Flyway 실행 (수동 제어가 필요한 경우)
     * 일반적으로는 자동 구성을 사용하지만, 특수한 경우 직접 제어 가능
     */
    // @Bean
    public Flyway customFlyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .baselineVersion(MigrationVersion.fromVersion("0"))
                .validateOnMigrate(true)
                .load();

        // 마이그레이션 정보 조회
        var info = flyway.info();
        System.out.println("적용된 마이그레이션 수: " + info.applied().length);
        System.out.println("대기 중인 마이그레이션 수: " + info.pending().length);

        // 마이그레이션 실행
        flyway.migrate();

        return flyway;
    }
}
