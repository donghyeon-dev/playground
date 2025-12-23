-- =============================================================================
-- V3__Add_user_profile_fields.sql
-- 사용자 프로필 필드 추가
-- =============================================================================

-- 사용자 테이블에 프로필 관련 컬럼 추가
ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);
ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500);
ALTER TABLE users ADD COLUMN bio TEXT;
ALTER TABLE users ADD COLUMN date_of_birth DATE;
ALTER TABLE users ADD COLUMN timezone VARCHAR(50) DEFAULT 'Asia/Seoul';
ALTER TABLE users ADD COLUMN locale VARCHAR(10) DEFAULT 'ko_KR';
ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN phone_verified BOOLEAN DEFAULT FALSE;

-- 인덱스 추가
CREATE INDEX idx_users_phone_number ON users(phone_number);
CREATE INDEX idx_users_email_verified ON users(email_verified);
