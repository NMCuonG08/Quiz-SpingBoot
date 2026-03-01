-- ============================================================
-- PostgreSQL Initialization Script
-- Chạy tự động khi container khởi động lần đầu
-- ============================================================

-- Tạo extension hữu ích
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";   -- Full-text search
CREATE EXTENSION IF NOT EXISTS "btree_gin"; -- Index optimization

-- Tạo schema nếu cần
-- CREATE SCHEMA IF NOT EXISTS employee_schema;

-- Thông báo hoàn thành
DO $$
BEGIN
    RAISE NOTICE '✅ PostgreSQL initialization completed for Employee DB';
END $$;
