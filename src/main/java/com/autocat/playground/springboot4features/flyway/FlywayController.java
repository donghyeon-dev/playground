package com.autocat.playground.springboot4features.flyway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Flyway 마이그레이션 정보 API 컨트롤러
 *
 * 마이그레이션 상태와 이력을 조회할 수 있는 REST API를 제공합니다.
 * 프로덕션에서는 적절한 인증/인가를 적용해야 합니다.
 */
@RestController
@RequestMapping("/api/v1/flyway")
public class FlywayController {

    private final FlywayInfoService flywayInfoService;

    public FlywayController(FlywayInfoService flywayInfoService) {
        this.flywayInfoService = flywayInfoService;
    }

    /**
     * 마이그레이션 요약 정보 조회
     * GET /api/v1/flyway/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<FlywayInfoService.MigrationSummary> getSummary() {
        return ResponseEntity.ok(flywayInfoService.getMigrationSummary());
    }

    /**
     * 모든 마이그레이션 이력 조회
     * GET /api/v1/flyway/migrations
     */
    @GetMapping("/migrations")
    public ResponseEntity<List<FlywayInfoService.MigrationDetail>> getAllMigrations() {
        return ResponseEntity.ok(flywayInfoService.getAllMigrations());
    }

    /**
     * 적용된 마이그레이션만 조회
     * GET /api/v1/flyway/migrations/applied
     */
    @GetMapping("/migrations/applied")
    public ResponseEntity<List<FlywayInfoService.MigrationDetail>> getAppliedMigrations() {
        return ResponseEntity.ok(flywayInfoService.getAppliedMigrations());
    }

    /**
     * 대기 중인 마이그레이션 조회
     * GET /api/v1/flyway/migrations/pending
     */
    @GetMapping("/migrations/pending")
    public ResponseEntity<List<FlywayInfoService.MigrationDetail>> getPendingMigrations() {
        return ResponseEntity.ok(flywayInfoService.getPendingMigrations());
    }

    /**
     * 현재 버전 조회
     * GET /api/v1/flyway/version
     */
    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> getCurrentVersion() {
        return ResponseEntity.ok(Map.of(
                "currentVersion", flywayInfoService.getCurrentVersion(),
                "schemaVersion", flywayInfoService.getSchemaVersion()
        ));
    }

    /**
     * 마이그레이션 유효성 검증
     * GET /api/v1/flyway/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<FlywayInfoService.ValidationResult> validate() {
        return ResponseEntity.ok(flywayInfoService.validate());
    }

    /**
     * 마이그레이션 상태별 통계
     * GET /api/v1/flyway/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        return ResponseEntity.ok(flywayInfoService.getMigrationStatistics());
    }
}
