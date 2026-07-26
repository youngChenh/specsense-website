-- Migration: Add del_flag column for soft delete
-- Date: 2026-07-26
-- Description: Add del_flag field to all tables for logical delete support
-- del_flag = 0 means not deleted, del_flag = 1 means deleted

USE specsense_db;

-- Category (产品分类)
ALTER TABLE category ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE category ADD INDEX idx_del_flag (del_flag);

-- Brand Category (品牌分类)
ALTER TABLE brand_category ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE brand_category ADD INDEX idx_del_flag (del_flag);

-- Brand (品牌)
ALTER TABLE brand ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE brand ADD INDEX idx_del_flag (del_flag);

-- Product (产品)
ALTER TABLE product ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE product ADD INDEX idx_del_flag (del_flag);

-- Banner (轮播图)
ALTER TABLE banner ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE banner ADD INDEX idx_del_flag (del_flag);

-- Navigation Menu (导航菜单)
ALTER TABLE navigation_menu ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE navigation_menu ADD INDEX idx_del_flag (del_flag);

-- News (新闻)
ALTER TABLE news ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE news ADD INDEX idx_del_flag (del_flag);

-- Application (应用领域)
ALTER TABLE application ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE application ADD INDEX idx_del_flag (del_flag);

-- Download (下载)
ALTER TABLE download ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE download ADD INDEX idx_del_flag (del_flag);

-- Service (服务) - 注意：service 是 MySQL 保留字，表名可能需要加反引号
ALTER TABLE `service` ADD COLUMN del_flag TINYINT DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除';
ALTER TABLE `service` ADD INDEX idx_del_flag (del_flag);

-- Verify the changes
SELECT 'Category' as tbl, COUNT(*) as rows, SUM(del_flag) as deleted_count FROM category
UNION ALL SELECT 'Brand_Category', COUNT(*), SUM(del_flag) FROM brand_category
UNION ALL SELECT 'Brand', COUNT(*), SUM(del_flag) FROM brand
UNION ALL SELECT 'Product', COUNT(*), SUM(del_flag) FROM product
UNION ALL SELECT 'Banner', COUNT(*), SUM(del_flag) FROM banner
UNION ALL SELECT 'Navigation_Menu', COUNT(*), SUM(del_flag) FROM navigation_menu
UNION ALL SELECT 'News', COUNT(*), SUM(del_flag) FROM news
UNION ALL SELECT 'Application', COUNT(*), SUM(del_flag) FROM application
UNION ALL SELECT 'Download', COUNT(*), SUM(del_flag) FROM download
UNION ALL SELECT 'Service', COUNT(*), SUM(del_flag) FROM `service`;
