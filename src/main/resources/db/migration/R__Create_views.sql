-- =============================================================================
-- R__Create_views.sql
-- 반복 마이그레이션 - 뷰 생성/갱신
--
-- 이 파일은 내용이 변경될 때마다 재실행됩니다.
-- 뷰, 함수, 프로시저 등 재생성 가능한 객체에 적합합니다.
-- =============================================================================

-- 기존 뷰 삭제 (있으면)
DROP VIEW IF EXISTS v_user_summary;
DROP VIEW IF EXISTS v_order_summary;
DROP VIEW IF EXISTS v_product_sales;

-- =============================================================================
-- 사용자 요약 뷰
-- =============================================================================
CREATE VIEW v_user_summary AS
SELECT
    u.id,
    u.username,
    u.email,
    u.full_name,
    u.status,
    u.created_at,
    u.last_login_at,
    (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count,
    (SELECT COALESCE(SUM(o.total_amount), 0) FROM orders o WHERE o.user_id = u.id) AS total_spent
FROM users u
WHERE u.status != 'DELETED';

-- =============================================================================
-- 주문 요약 뷰
-- =============================================================================
CREATE VIEW v_order_summary AS
SELECT
    o.id,
    o.order_number,
    o.status,
    o.total_amount,
    o.ordered_at,
    u.username AS customer_name,
    u.email AS customer_email,
    (SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.id) AS item_count,
    (SELECT SUM(oi.quantity) FROM order_items oi WHERE oi.order_id = o.id) AS total_quantity
FROM orders o
JOIN users u ON o.user_id = u.id;

-- =============================================================================
-- 상품 판매 통계 뷰
-- =============================================================================
CREATE VIEW v_product_sales AS
SELECT
    p.id,
    p.name,
    p.category,
    p.price AS current_price,
    p.stock_quantity,
    COALESCE(SUM(oi.quantity), 0) AS total_sold,
    COALESCE(SUM(oi.subtotal), 0) AS total_revenue,
    COUNT(DISTINCT oi.order_id) AS order_count
FROM products p
LEFT JOIN order_items oi ON p.id = oi.product_id
LEFT JOIN orders o ON oi.order_id = o.id AND o.status NOT IN ('CANCELLED', 'REFUNDED')
GROUP BY p.id, p.name, p.category, p.price, p.stock_quantity;
