-- 删除不再使用的产品字段
ALTER TABLE product
    DROP COLUMN IF EXISTS detail_desc_en,
    DROP COLUMN IF EXISTS detail_desc_zh,
    DROP COLUMN IF EXISTS detailed_specs,
    DROP COLUMN IF EXISTS alibaba_images,
    DROP COLUMN IF EXISTS external_images;
