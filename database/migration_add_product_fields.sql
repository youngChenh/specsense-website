-- Migration: Add highlights and applications fields to product table
-- Date: 2026-07-26
-- Description: Add highlights and applications fields for product details page

USE specsense_db;

-- Product table - add new fields
ALTER TABLE product ADD COLUMN highlights TEXT COMMENT '产品亮点';
ALTER TABLE product ADD COLUMN applications TEXT COMMENT '应用范围';

-- Verify the changes
DESCRIBE product;
