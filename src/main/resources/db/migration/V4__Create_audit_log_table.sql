-- =============================================================================
-- V4__Create_audit_log_table.sql
-- 감사 로그 테이블 생성
-- =============================================================================

-- 감사 로그 테이블
CREATE TABLE audit_logs (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type     VARCHAR(100) NOT NULL,
    entity_id       BIGINT NOT NULL,
    action          VARCHAR(20) NOT NULL,
    actor_id        BIGINT,
    actor_type      VARCHAR(50),
    old_values      TEXT,
    new_values      TEXT,
    ip_address      VARCHAR(45),
    user_agent      VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_audit_action CHECK (action IN ('CREATE', 'UPDATE', 'DELETE', 'READ', 'LOGIN', 'LOGOUT'))
);

-- 파티션을 위한 인덱스 (성능 최적화)
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

-- 복합 인덱스 (자주 사용되는 쿼리 패턴용)
CREATE INDEX idx_audit_logs_entity_time ON audit_logs(entity_type, created_at);
