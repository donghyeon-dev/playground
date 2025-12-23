/**
 * Spring Boot 4.0 - Flyway 데이터베이스 마이그레이션 예시 패키지
 *
 * <h2>Flyway란?</h2>
 * <p>Flyway는 데이터베이스 스키마 버전 관리 및 마이그레이션 도구입니다.
 * SQL 스크립트나 Java 코드를 통해 데이터베이스 스키마를 버전 관리하고
 * 자동으로 마이그레이션을 수행합니다.</p>
 *
 * <h2>Spring Boot 4.0에서의 개선사항</h2>
 * <ul>
 *   <li>Flyway 10.x 버전 지원</li>
 *   <li>GraalVM Native Image 완벽 지원</li>
 *   <li>Virtual Threads와 통합</li>
 *   <li>개선된 자동 구성 및 설정 옵션</li>
 *   <li>다중 데이터소스 마이그레이션 지원 강화</li>
 *   <li>Flyway Teams/Enterprise 기능 통합 개선</li>
 * </ul>
 *
 * <h2>마이그레이션 파일 명명 규칙</h2>
 * <pre>
 * V{버전}__{설명}.sql          - 버전 마이그레이션 (한 번만 실행)
 * U{버전}__{설명}.sql          - 언두 마이그레이션 (롤백용, Teams 전용)
 * R__{설명}.sql                - 반복 마이그레이션 (변경 시 재실행)
 *
 * 예시:
 * V1__Create_user_table.sql
 * V2__Add_email_column.sql
 * V1.1__Add_index.sql
 * R__Update_views.sql
 * </pre>
 *
 * <h2>마이그레이션 파일 위치</h2>
 * <pre>
 * src/main/resources/db/migration/    - 기본 위치
 * </pre>
 */
package com.autocat.playground.springboot4features.flyway;
