package com.autocat.playground.springboot4features.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;
import org.flywaydb.core.api.MigrationState;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Flyway 마이그레이션 정보 조회 서비스
 *
 * 현재 마이그레이션 상태, 이력, 대기 중인 마이그레이션 등을
 * 조회할 수 있는 서비스입니다.
 */
@Service
public class FlywayInfoService {

    private final Flyway flyway;

    public FlywayInfoService(Flyway flyway) {
        this.flyway = flyway;
    }

    /**
     * 마이그레이션 요약 정보 조회
     */
    public MigrationSummary getMigrationSummary() {
        MigrationInfoService info = flyway.info();

        return new MigrationSummary(
                info.current() != null ? info.current().getVersion().getVersion() : "없음",
                info.applied().length,
                info.pending().length,
                info.all().length,
                getSchemaVersion()
        );
    }

    /**
     * 모든 마이그레이션 이력 조회
     */
    public List<MigrationDetail> getAllMigrations() {
        return Arrays.stream(flyway.info().all())
                .map(this::toMigrationDetail)
                .collect(Collectors.toList());
    }

    /**
     * 적용된 마이그레이션만 조회
     */
    public List<MigrationDetail> getAppliedMigrations() {
        return Arrays.stream(flyway.info().applied())
                .map(this::toMigrationDetail)
                .collect(Collectors.toList());
    }

    /**
     * 대기 중인 마이그레이션 조회
     */
    public List<MigrationDetail> getPendingMigrations() {
        return Arrays.stream(flyway.info().pending())
                .map(this::toMigrationDetail)
                .collect(Collectors.toList());
    }

    /**
     * 현재 버전 조회
     */
    public String getCurrentVersion() {
        MigrationInfo current = flyway.info().current();
        return current != null ? current.getVersion().getVersion() : "없음";
    }

    /**
     * 스키마 버전 조회
     */
    public String getSchemaVersion() {
        MigrationInfo current = flyway.info().current();
        if (current == null) {
            return "스키마 없음";
        }
        return current.getVersion().getVersion() + " (" + current.getDescription() + ")";
    }

    /**
     * 마이그레이션 유효성 검증
     */
    public ValidationResult validate() {
        try {
            flyway.validate();
            return new ValidationResult(true, "마이그레이션 검증 성공", List.of());
        } catch (Exception e) {
            return new ValidationResult(false, e.getMessage(), List.of(e.getMessage()));
        }
    }

    /**
     * 마이그레이션 상태별 통계
     */
    public Map<String, Long> getMigrationStatistics() {
        return Arrays.stream(flyway.info().all())
                .collect(Collectors.groupingBy(
                        info -> info.getState().getDisplayName(),
                        Collectors.counting()
                ));
    }

    private MigrationDetail toMigrationDetail(MigrationInfo info) {
        return new MigrationDetail(
                info.getVersion() != null ? info.getVersion().getVersion() : "R",
                info.getDescription(),
                info.getType().name(),
                info.getState().getDisplayName(),
                info.getInstalledOn() != null ? info.getInstalledOn().toString() : null,
                info.getExecutionTime() != null ? info.getExecutionTime() : 0,
                info.getState() == MigrationState.SUCCESS,
                info.getPhysicalLocation()
        );
    }

    // DTO Records
    public record MigrationSummary(
            String currentVersion,
            int appliedCount,
            int pendingCount,
            int totalCount,
            String schemaVersion
    ) {
    }

    public record MigrationDetail(
            String version,
            String description,
            String type,
            String state,
            String installedOn,
            int executionTimeMs,
            boolean success,
            String location
    ) {
    }

    public record ValidationResult(
            boolean valid,
            String message,
            List<String> errors
    ) {
    }
}
