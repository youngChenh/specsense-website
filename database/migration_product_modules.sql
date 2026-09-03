-- 产品详情模块化字段
ALTER TABLE products ADD COLUMN overview_modules TEXT COMMENT '产品详情模块化JSON';
-- 保留原有的 overview_en/overview_zh 字段（可选删除）
