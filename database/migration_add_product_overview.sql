-- 产品概览富文本字段迁移
-- 用于替代 PDF 内嵌展示，支持图片、文字、表格

ALTER TABLE products ADD COLUMN overview_en TEXT COMMENT '产品概览富文本(英文)';
ALTER TABLE products ADD COLUMN overview_zh TEXT COMMENT '产品概览富文本(中文)';

-- 可选：后续删除不再使用的 pdf_urls 字段
-- ALTER TABLE products DROP COLUMN pdf_urls;
