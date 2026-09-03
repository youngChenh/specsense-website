-- Migration: Add PDF-related and product detail page fields
-- Date: 2026-08-31
-- Description:
--   1. Add download_pdf_url for separate download PDF
--   2. Add detail_desc_en / detail_desc_zh for detailed product description text
--   3. Add detailed_specs for more detailed parameters (separate from simple specs)
--   4. Add alibaba_images for Alibaba-style product images (up to 10)
--   5. Add external_images for images with external links [{url, link}]

USE youthful_db;

ALTER TABLE product
    ADD COLUMN detail_desc_en    TEXT        COMMENT '产品详细描述文字-英文' AFTER description_zh,
    ADD COLUMN detail_desc_zh    TEXT        COMMENT '产品详细描述文字-中文' AFTER detail_desc_en,
    ADD COLUMN detailed_specs    JSON        COMMENT '详细参数（独立字段）' AFTER specs_json,
    ADD COLUMN alibaba_images    JSON        COMMENT '阿里10图（最多10张URL数组）' AFTER image_urls,
    ADD COLUMN external_images   JSON        COMMENT '带外链的图片[{url, link}]' AFTER alibaba_images;

-- Verify
DESCRIBE product;