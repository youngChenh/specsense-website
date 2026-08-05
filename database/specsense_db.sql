-- SpecSense Database Schema and Initial Data
-- MySQL 8.0

CREATE DATABASE IF NOT EXISTS specsense_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE specsense_db;

-- ----------------------------
-- Table: category (Product categories with tree structure)
-- ----------------------------
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name_en VARCHAR(100) NOT NULL COMMENT 'Category name in English',
    name_zh VARCHAR(100) NOT NULL COMMENT 'Category name in Chinese',
    `key` VARCHAR(50) NOT NULL COMMENT 'Category key (url-friendly)',
    parent_id BIGINT DEFAULT NULL COMMENT 'Parent category ID (NULL for root)',
    sort_order INT DEFAULT 0 COMMENT 'Display order',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent_id (parent_id),
    INDEX idx_key (`key`),
    UNIQUE KEY uk_key_parent (`key`, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Product categories';

-- ----------------------------
-- Table: product
-- ----------------------------
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT 'Category ID',
    name_en VARCHAR(200) NOT NULL COMMENT 'Product name in English',
    name_zh VARCHAR(200) NOT NULL COMMENT 'Product name in Chinese',
    slug VARCHAR(200) NOT NULL COMMENT 'URL-friendly identifier',
    description_en TEXT COMMENT 'Description in English',
    description_zh TEXT COMMENT 'Description in Chinese',
    image_url VARCHAR(500) COMMENT 'Product image URL',
    image_urls TEXT COMMENT 'Multiple image URLs as JSON array',
    pdf_urls TEXT COMMENT 'PDF download URLs as JSON array',
    specs_json JSON COMMENT 'Technical specifications as JSON',
    featured BOOLEAN DEFAULT FALSE COMMENT 'Show on homepage',
    sort_order INT DEFAULT 0 COMMENT 'Display order',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_id (category_id),
    INDEX idx_slug (slug),
    INDEX idx_featured (featured),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Products';

-- ----------------------------
-- Table: brand_category (Brand categories)
-- ----------------------------
DROP TABLE IF EXISTS brand_category;
CREATE TABLE brand_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name_en VARCHAR(100) NOT NULL COMMENT 'Category name in English',
    name_zh VARCHAR(100) NOT NULL COMMENT 'Category name in Chinese',
    `key` VARCHAR(50) NOT NULL COMMENT 'Category key (url-friendly)',
    sort_order INT DEFAULT 0 COMMENT 'Display order',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_key (`key`),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Brand categories';

-- ----------------------------
-- Table: brand (Agency brands)
-- ----------------------------
DROP TABLE IF EXISTS brand;
CREATE TABLE brand (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT 'Brand category ID',
    name_en VARCHAR(200) NOT NULL COMMENT 'Brand name in English',
    name_zh VARCHAR(200) NOT NULL COMMENT 'Brand name in Chinese',
    slug VARCHAR(200) NOT NULL COMMENT 'URL-friendly identifier',
    logo_url VARCHAR(500) COMMENT 'Brand logo URL',
    description_en TEXT COMMENT 'Description in English',
    description_zh TEXT COMMENT 'Description in Chinese',
    website_url VARCHAR(500) COMMENT 'Brand website URL',
    featured BOOLEAN DEFAULT FALSE COMMENT 'Show as featured',
    sort_order INT DEFAULT 0 COMMENT 'Display order',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_id (category_id),
    INDEX idx_slug (slug),
    INDEX idx_featured (featured),
    FOREIGN KEY (category_id) REFERENCES brand_category(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agency brands';

-- ----------------------------
-- Table: news
-- ----------------------------
DROP TABLE IF EXISTS news;
CREATE TABLE news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) NOT NULL COMMENT 'News category: company, industry, technology',
    title_en VARCHAR(300) NOT NULL COMMENT 'Title in English',
    title_zh VARCHAR(300) NOT NULL COMMENT 'Title in Chinese',
    slug VARCHAR(300) NOT NULL COMMENT 'URL-friendly identifier',
    excerpt_en TEXT COMMENT 'Excerpt in English',
    excerpt_zh TEXT COMMENT 'Excerpt in Chinese',
    content_en LONGTEXT COMMENT 'Full content in English',
    content_zh LONGTEXT COMMENT 'Full content in Chinese',
    image_url VARCHAR(500) COMMENT 'Featured image URL',
    author VARCHAR(100) DEFAULT 'SpecSense' COMMENT 'Author name',
    published_at DATETIME COMMENT 'Publication date',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_slug (slug),
    INDEX idx_published_at (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='News and articles';

-- ----------------------------
-- Table: application
-- ----------------------------
DROP TABLE IF EXISTS application;
CREATE TABLE application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    `type` VARCHAR(50) NOT NULL COMMENT 'Application type key',
    title_en VARCHAR(200) NOT NULL COMMENT 'Title in English',
    title_zh VARCHAR(200) NOT NULL COMMENT 'Title in Chinese',
    description_en TEXT COMMENT 'Description in English',
    description_zh TEXT COMMENT 'Description in Chinese',
    icon VARCHAR(50) DEFAULT 'default' COMMENT 'Icon class or key',
    image_url VARCHAR(500) COMMENT 'Application image URL',
    linked_category_keys VARCHAR(500) COMMENT 'Comma-separated category keys for related products',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_type (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Application areas';

-- ----------------------------
-- Table: service
-- ----------------------------
DROP TABLE IF EXISTS service;
CREATE TABLE service (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    `type` VARCHAR(50) NOT NULL COMMENT 'Service type key',
    title_en VARCHAR(200) NOT NULL COMMENT 'Title in English',
    title_zh VARCHAR(200) NOT NULL COMMENT 'Title in Chinese',
    description_en TEXT COMMENT 'Description in English',
    description_zh TEXT COMMENT 'Description in Chinese',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_type (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Services';

-- ----------------------------
-- Table: download
-- ----------------------------
DROP TABLE IF EXISTS download;
CREATE TABLE download (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) NOT NULL COMMENT 'Download category: manuals, software, datasheets, catalogs',
    name_en VARCHAR(200) NOT NULL COMMENT 'File name in English',
    name_zh VARCHAR(200) NOT NULL COMMENT 'File name in Chinese',
    description_en TEXT COMMENT 'Description in English',
    description_zh TEXT COMMENT 'Description in Chinese',
    file_url VARCHAR(500) NOT NULL COMMENT 'File download URL',
    file_size VARCHAR(50) COMMENT 'File size (e.g., 2.5MB)',
    original_filename VARCHAR(255) COMMENT 'Original uploaded filename with extension',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Downloadable files';

-- ----------------------------
-- Table: company_info
-- ----------------------------
DROP TABLE IF EXISTS company_info;
CREATE TABLE company_info (
    id BIGINT PRIMARY KEY DEFAULT 1,
    image_url VARCHAR(500) COMMENT 'Company overview image URL',
    about_en TEXT COMMENT 'About section description in English',
    about_zh TEXT COMMENT 'About section description in Chinese',
    description_en TEXT COMMENT 'Company description in English',
    description_zh TEXT COMMENT 'Company description in Chinese',
    mission_en TEXT COMMENT 'Mission in English',
    mission_zh TEXT COMMENT 'Mission in Chinese',
    email VARCHAR(200) COMMENT 'Contact email',
    phone VARCHAR(100) COMMENT 'Contact phone',
    address_en VARCHAR(500) COMMENT 'Company address in English',
    address_zh VARCHAR(500) COMMENT 'Company address in Chinese',
    values_json JSON COMMENT 'Company values as JSON array',
    timeline_json JSON COMMENT 'Timeline milestones as JSON array',
    qualifications_json JSON COMMENT 'Qualifications as JSON array',
    partners_stats_json JSON COMMENT 'Partners statistics as JSON',
    stats_json JSON COMMENT 'Company stats as JSON (years, countries, products, engineers)',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (id = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Company information';

-- ----------------------------
-- Table: navigation_menu
-- ----------------------------
DROP TABLE IF EXISTS navigation_menu;
CREATE TABLE navigation_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    `key` VARCHAR(50) NOT NULL COMMENT 'Menu key (e.g., header-home, footer-products)',
    position VARCHAR(20) NOT NULL COMMENT 'Position: header, footer',
    label_en VARCHAR(100) NOT NULL COMMENT 'Label in English',
    label_zh VARCHAR(100) NOT NULL COMMENT 'Label in Chinese',
    path VARCHAR(200) COMMENT 'Link path',
    icon VARCHAR(50) COMMENT 'Icon class or key',
    sort_order INT DEFAULT 0 COMMENT 'Display order',
    parent_id BIGINT DEFAULT NULL COMMENT 'Parent menu ID for submenus',
    enabled BOOLEAN DEFAULT TRUE COMMENT 'Whether the menu is enabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_position (position),
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Navigation menus';

-- ----------------------------
-- Table: contact_inquiry
-- ----------------------------
DROP TABLE IF EXISTS contact_inquiry;
CREATE TABLE contact_inquiry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT 'Contact name',
    email VARCHAR(200) NOT NULL COMMENT 'Contact email',
    company VARCHAR(200) COMMENT 'Company name',
    product VARCHAR(300) COMMENT 'Product of interest',
    message TEXT NOT NULL COMMENT 'Inquiry message',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT 'Logical delete: 0=not deleted, 1=deleted',
    INDEX idx_created_at (created_at),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Contact form submissions';

-- ----------------------------
-- Table: admin_user
-- ----------------------------
DROP TABLE IF EXISTS admin_user;
CREATE TABLE admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT 'Username',
    password_hash VARCHAR(255) NOT NULL COMMENT 'BCrypt password hash',
    role VARCHAR(20) DEFAULT 'admin' COMMENT 'User role: admin, editor',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Admin users';

-- ----------------------------
-- Table: banner
-- ----------------------------
DROP TABLE IF EXISTS banner;
CREATE TABLE banner (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    image_url VARCHAR(500) NOT NULL COMMENT 'Banner image URL',
    link_url VARCHAR(500) COMMENT 'Click-through link URL',
    title_en VARCHAR(100) COMMENT 'Title in English',
    title_zh VARCHAR(100) COMMENT 'Title in Chinese',
    subtitle_en VARCHAR(200) COMMENT 'Subtitle in English',
    subtitle_zh VARCHAR(200) COMMENT 'Subtitle in Chinese',
    sort_order INT DEFAULT 0 COMMENT 'Display order, smaller first',
    enabled BOOLEAN DEFAULT TRUE COMMENT 'Whether the banner is enabled',
    locale VARCHAR(10) DEFAULT 'zh' COMMENT 'Language: zh, en',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sort_order (sort_order),
    INDEX idx_enabled (enabled),
    INDEX idx_locale (locale)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Homepage banners';

-- =====================================================
-- INITIAL DATA
-- =====================================================

-- Admin user (password: specsense123)
INSERT INTO admin_user (username, password_hash, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3eSu9a9J9mPZvQEfnNxu', 'admin');

-- Homepage Banners
INSERT INTO banner (image_url, link_url, title_en, title_zh, subtitle_en, subtitle_zh, sort_order, enabled, locale) VALUES
('https://images.unsplash.com/photo-1518770660439-4636190af475?w=1920&h=600&fit=crop', '/products', 'Precision Photoelectric Instruments', '精密光电仪器', 'Providing high-quality photoelectric detection solutions', '为您提供高品质的光电检测解决方案', 1, TRUE, 'zh'),
('https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=1920&h=600&fit=crop', '/products', 'Advanced Technology', '先进技术', 'Integrating innovative technology to empower industry upgrading', '融合创新科技，助力产业升级', 2, TRUE, 'zh'),
('https://images.unsplash.com/photo-1581092160562-40aa08e78837?w=1920&h=600&fit=crop', '/contact', 'Global Service', '全球服务', 'Trusted partner serving 100+ countries and regions', '服务100+国家和地区，值得信赖', 3, TRUE, 'zh');

-- Company Info
INSERT INTO company_info (id, image_url, about_en, about_zh, description_en, description_zh, mission_en, mission_zh, email, phone, address_en, address_zh, values_json, timeline_json, qualifications_json, partners_stats_json, stats_json) VALUES
(1,
'https://images.unsplash.com/photo-1497366216548-37526070297c?w=600&h=400&fit=crop',
'SpecSense Technologies Inc. is a high-tech enterprise specializing in the R&D and production of photoelectric instruments and sensors.',
'思谱传感科技有限公司是一家专业从事光电仪器和传感器研发、生产的高新技术企业。',
'Founded with a vision to provide world-class photoelectric measurement solutions, SpecSense has grown from a small research team to a global enterprise serving customers in over 100 countries. Our commitment to quality and innovation has made us a trusted partner for scientists and engineers worldwide. Our state-of-the-art manufacturing facility in Shenzhen combines advanced production technologies with rigorous quality control to deliver instruments that meet the highest international standards.',
'思谱科技致力于提供世界一流的光电测量解决方案，从一个小规模研发团队发展成为为全球100多个国家客户提供服务的跨国企业。我们对质量和创新的执着追求使我们在全球范围内成为科学家和工程师值得信赖的合作伙伴。位于深圳的先进生产设施将先进生产技术与严格的质量控制相结合，交付满足最高国际标准的仪器。',
'To provide cutting-edge spectroscopic solutions that empower researchers and industries worldwide to achieve precise, reliable, and sustainable analysis.',
'为全球研究人员和行业提供尖端的光谱解决方案，帮助他们实现精确、可靠和可持续的分析。',
'info@specsense.com',
'+86 400-888-8888',
'Bldg A, 2nd Floor, Hi-Tech Industrial Park, Nanshan District, Shenzhen, China',
'深圳市南山区高新技术产业园区A栋2楼',
'["Innovation", "Quality", "Integrity", "Customer Success"]',
'[{"year": "2008", "event_en": "Founded in Shenzhen", "event_zh": "成立于深圳"}, {"year": "2012", "event_en": "Launched first spectrometer product line", "event_zh": "推出首款光谱仪产品线"}, {"year": "2016", "event_en": "Established R&D center", "event_zh": "成立研发中心"}, {"year": "2020", "event_en": "Expanded to global markets", "event_zh": "扩展至全球市场"}, {"year": "2024", "event_en": "Launched next-gen mini spectrometer", "event_zh": "推出下一代微型光谱仪"}]',
'["ISO 9001:2015", "CE Certification", "RoHS Compliance"]',
'{"researchInstitutions": "50+", "countries": "100+", "customers": "1000+"}',
'{"years": "18+", "countries": "100+", "products": "200+", "engineers": "50+"}'
);

-- Navigation Menus (Header)
INSERT INTO navigation_menu (`key`, position, label_en, label_zh, path, sort_order, enabled) VALUES
('header-home', 'header', 'Home', '首页', '/', 1, TRUE),
('header-products', 'header', 'Products', '产品中心', '/products', 2, TRUE),
('header-applications', 'header', 'Applications', '应用领域', '/applications', 3, TRUE),
('header-services', 'header', 'Services', '服务与支持', '/services', 4, TRUE),
('header-brands', 'header', 'Agency Brand', '代理品牌', '/brands', 5, TRUE),
('header-news', 'header', 'News', '新闻资讯', '/news', 6, TRUE),
('header-about', 'header', 'About Us', '关于我们', '/about', 7, TRUE),
('header-contact', 'header', 'Contact', '联系我们', '/contact', 8, TRUE);

-- Navigation Menus (Footer)
INSERT INTO navigation_menu (`key`, position, label_en, label_zh, path, sort_order, enabled) VALUES
('footer-products', 'footer', 'Products', '产品', '/products', 1, TRUE),
('footer-applications', 'footer', 'Applications', '应用领域', '/applications', 2, TRUE),
('footer-services', 'footer', 'Services', '服务与支持', '/services', 3, TRUE),
('footer-about', 'footer', 'About Us', '关于我们', '/about', 4, TRUE),
('footer-contact', 'footer', 'Contact', '联系我们', '/contact', 5, TRUE);

-- Categories (5 main + 16 sub-categories)
INSERT INTO category (name_en, name_zh, `key`, parent_id, sort_order) VALUES
-- Main categories
('Spectrometers', '光谱仪', 'spectrometer', NULL, 1),
('Analyzers', '分析仪', 'analyzer', NULL, 2),
('Sensors', '传感器', 'sensor', NULL, 3),
('Light Sources', '光源', 'lightsource', NULL, 4),
('Accessories', '配件', 'accessory', NULL, 5);

-- Sub-categories (parent_id references parent category)
INSERT INTO category (name_en, name_zh, `key`, parent_id, sort_order) VALUES
-- Spectrometer sub-categories
('UV-Visible Spectrometers', '紫外可见光谱仪', 'spectrometer-uv', 1, 1),
('NIR Spectrometers', '近红外光谱仪', 'spectrometer-nir', 1, 2),
('Raman Spectrometers', '拉曼光谱仪', 'spectrometer-raman', 1, 3),
('Miniature Spectrometers', '微型光谱仪', 'spectrometer-mini', 1, 4),
-- Analyzer sub-categories
('Photoelectric Analyzers', '光电分析仪', 'analyzer-photoelectric', 2, 1),
('Photometers', '光度计', 'analyzer-photometer', 2, 2),
('Color Analyzers', '色度分析仪', 'analyzer-color', 2, 3),
-- Sensor sub-categories
('Fiber Optic Sensors', '光纤传感器', 'sensor-fiber', 3, 1),
('Photoelectric Sensors', '光电传感器', 'sensor-photoelectric', 3, 2),
('Displacement Sensors', '位移传感器', 'sensor-displacement', 3, 3),
-- Light source sub-categories
('Lasers', '激光器', 'lightsource-laser', 4, 1),
('LED Light Sources', 'LED光源', 'lightsource-led', 4, 2),
('Halogen Light Sources', '卤素光源', 'lightsource-halogen', 4, 3),
-- Accessory sub-categories
('Fiber Optic Probes', '光纤探头', 'accessory-fiberprobe', 5, 1),
('Holders & Mounts', '支架与固定装置', 'accessory-holder', 5, 2),
('Couplers', '耦合器', 'accessory-coupler', 5, 3);

-- Products
INSERT INTO product (category_id, name_en, name_zh, slug, description_en, description_zh, image_url, image_urls, pdf_urls, specs_json, featured, sort_order) VALUES
-- Spectrometer products
(6, 'EA-One UV-Vis Spectrometer', 'EA-One 紫外可见光谱仪', 'ea-one-spectrometer',
'High-resolution UV-Visible spectrometer with exceptional wavelength accuracy from 190-1100nm. Ideal for academic research and quality control laboratories.',
'高分辨率紫外可见光谱仪，波长精度190-1100nm，适用于学术研究和质量控制实验室。',
'https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=600',
'["https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=800","https://images.unsplash.com/photo-1581093450021-4a7360e9a6b5?w=800","https://images.unsplash.com/photo-1518152006812-edab29b069ac?w=800"]',
'["/downloads/datasheets/ea-one-datasheet.pdf","/downloads/manuals/ea-one-manual.pdf"]',
'{"wavelength_range": "190-1100nm", "resolution": "1nm", "scan_rate": "1000nm/min", "weight": "12kg", "dimensions": "450×350×180mm"}',
TRUE, 1),

(7, 'NIR-Pro FT-NIR Spectrometer', 'NIR-Pro 傅立叶近红外光谱仪', 'nir-pro-spectrometer',
'Fourier Transform NIR spectrometer for rapid, non-destructive analysis of agricultural products, pharmaceuticals, and chemical compounds.',
'傅立叶变换近红外光谱仪，用于农产品、药品和化学品的快速、无损分析。',
'https://images.unsplash.com/photo-1581093450021-4a7360e9a6b5?w=600',
NULL,
NULL,
'{"wavelength_range": "800-2500nm", "resolution": "4cm-1", "scan_rate": "20 spectra/sec", "weight": "18kg", "dimensions": "520×420×220mm"}',
TRUE, 2),

(8, 'Raman-785 Portable Raman Spectrometer', 'Raman-785 便携式拉曼光谱仪', 'raman-785-spectrometer',
'Compact, portable Raman spectrometer with 785nm laser for on-site material identification and verification in pharmaceutical, law enforcement, and珠宝检测。',
'紧凑型便携式拉曼光谱仪，785nm激光源，适用于制药、执法和珠宝检测的现场材料鉴定。',
'https://images.unsplash.com/photo-1576086213369-97a306d36557?w=600',
NULL,
NULL,
'{"laser_wavelength": "785nm", "raman_range": "200-2500cm-1", "resolution": "4cm-1", "weight": "2.5kg", "battery_life": "8hours"}',
FALSE, 3),

(9, 'Mini-Spec Pocket Spectrometer', 'Mini-Spec 口袋光谱仪', 'mini-spec-spectrometer',
'Ultra-compact miniature spectrometer designed for field work and educational purposes. USB powered with smartphone app integration.',
'超紧凑型微型光谱仪，专为现场工作和教育目的设计。USB供电，支持智能手机应用集成。',
'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600',
NULL,
NULL,
'{"wavelength_range": "400-700nm", "resolution": "3nm", "interface": "USB-C", "weight": "0.3kg", "dimensions": "100×60×25mm"}',
TRUE, 4),

-- Analyzer products
(10, 'PE-200 Photoelectric Analyzer', 'PE-200 光电分析仪', 'pe-200-analyzer',
'High-precision photoelectric analyzer for measuring optical density, transmittance, and reflectance with automated sample positioning.',
'高精度光电分析仪，用于测量光密度、透射率和反射率，配有自动样品定位功能。',
'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=600',
NULL,
NULL,
'{"measurement_range": "0-4OD", "accuracy": "±0.01OD", "repeatability": "±0.005OD", "weight": "8kg", "dimensions": "380×280×150mm"}',
FALSE, 1),

(11, 'PM-100 Digital Photometer', 'PM-100 数字光度计', 'pm-100-photometer',
'Digital photometer with wide dynamic range for colorimetry and radiometry measurements in lighting, display, and solar cell industries.',
'宽动态范围数字光度计，用于照明、显示和太阳能电池行业的色度测量和辐射测量。',
'https://images.unsplash.com/photo-1504868584819-f8e8b4b6d7e3?w=600',
NULL,
NULL,
'{"measurement_range": "0.01-100000 lux", "accuracy": "±3%", "detector": "Silicon photodiode", "weight": "0.5kg", "dimensions": "180×80×50mm"}',
FALSE, 2),

(12, 'CS-200 Color Spectrophotometer', 'CS-200 色度分光光度计', 'cs-200-color-analyzer',
'Professional color spectrophotometer for precise color measurement in paint, textile, plastic, and food industries with 45°/0° geometry.',
'专业色度分光光度计，适用于油漆、纺织、塑料和食品行业的精确颜色测量，采用45°/0°几何结构。',
'https://images.unsplash.com/photo-1581093450021-4a7360e9a6b5?w=600',
NULL,
NULL,
'{"wavelength_range": "360-780nm", "repeatability": "ΔE<0.02", "measurement_geometry": "45°/0°", "weight": "4.5kg", "dimensions": "300×200×120mm"}',
FALSE, 3),

-- Sensor products
(13, 'FO-S100 Fiber Optic Spectrometer', 'FO-S100 光纤光谱仪', 'fo-s100-fiber-sensor',
'Fiber optic spectrometer probe with SMA905 connector for remote sensing and in-situ measurements in harsh environments.',
'SMA905接口光纤光谱仪探头，用于恶劣环境中的远程传感和现场测量。',
'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600',
NULL,
NULL,
'{"fiber_core": "100μm", "connector": "SMA905", "cable_length": "1m (standard)", "temperature_range": "-40 to 200°C", "weight": "0.2kg"}',
FALSE, 1),

(14, 'PE-S50 Photoelectric Sensor', 'PE-S50 光电传感器', 'pe-s50-photoelectric-sensor',
'High-speed photoelectric sensor with digital output for detection, counting, and positioning in automated manufacturing lines.',
'高速光电传感器，数字输出，用于自动化生产线中的检测、计数和定位。',
'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=600',
NULL,
NULL,
'{"detection_range": "0-500mm", "response_time": "0.5ms", "output": "NPN/PNP", "supply_voltage": "12-24VDC", "protection": "IP67"}',
FALSE, 2),

(15, 'DS-100 Displacement Sensor', 'DS-100 位移传感器', 'ds-100-displacement-sensor',
'Non-contact laser displacement sensor for precision measurement of position, thickness, and vibration in industrial quality control.',
'非接触式激光位移传感器，用于工业质量控制中的位置、厚度和振动精密测量。',
'https://images.unsplash.com/photo-1565361484688-a3d1ae0c31f7?w=600',
NULL,
NULL,
'{"measurement_range": "±10mm", "resolution": "0.1μm", "linearity": "±0.1%", "sampling_rate": "2kHz", "laser_class": "Class II"}',
FALSE, 3),

-- Light source products
(16, 'L-785nm Laser Diode Module', 'L-785nm 激光二极管模块', 'l-785nm-laser',
'High-power 785nm laser diode module for Raman spectroscopy excitation with integrated temperature control.',
'高功率785nm激光二极管模块，用于拉曼光谱激发，内置温度控制。',
'https://images.unsplash.com/photo-1576086213369-97a306d36557?w=600',
NULL,
NULL,
'{"wavelength": "785nm±0.5nm", "output_power": "500mW", "operating_current": "450mA", "operating_voltage": "2.2V", "package": "TO-8"}',
FALSE, 1),

(17, 'LED-400nm High-Power LED', 'LED-400nm 大功率LED', 'led-400nm-high-power',
'High-power 400nm LED for fluorescence excitation and UV curing applications with excellent stability.',
'高功率400nm LED，用于荧光激发和紫外固化应用，具有出色的稳定性。',
'https://images.unsplash.com/photo-1565361484688-a3d1ae0c31f7?w=600',
NULL,
NULL,
'{"wavelength": "400nm±5nm", "output_power": "1000mW", "viewing_angle": "120°", "forward_current": "350mA", "operating_temp": "-20 to 60°C"}',
FALSE, 2),

(18, 'HL-100 Halogen Light Source', 'HL-100 卤素光源', 'hl-100-halogen-light',
'High-stability halogen light source with 3000K color temperature for VIS-NIR spectroscopy applications.',
'高稳定性卤素光源，色温3000K，适用于可见-近红外光谱应用。',
'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600',
NULL,
NULL,
'{"color_temperature": "3000K", "power": "100W", "lifetime": "2000hrs", "filament": "Tungsten", "base": "GX6.35"}',
FALSE, 3),

-- Accessory products
(19, 'FIP-200 Fiber Optic Probe', 'FIP-200 光纤探头', 'fip-200-fiber-probe',
'Versatile fiber optic probe with adjustable working distance for transmission and reflection measurements.',
'多功能光纤探头，可调工作距离，用于透射和反射测量。',
'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600',
NULL,
NULL,
'{"fiber_core": "200μm", "numerical_aperture": "0.22", "working_distance": "0.5-10mm", "connector": "SMA905", "cable_length": "1m"}',
FALSE, 1),

(20, 'HM-50 Probe Holder', 'HM-50 探头支架', 'hm-50-probe-holder',
'Precision 3-axis probe holder with magnetic base for stable positioning in various measurement setups.',
'精密三轴探头支架，磁性底座，用于各种测量设置中的稳定定位。',
'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=600',
NULL,
NULL,
'{"axes": "3-axis", "base": "Magnetic", "fine_adjustment": "Micrometer", "load_capacity": "2kg", "weight": "1.2kg"}',
FALSE, 2),

(21, 'YC-2 Fiber Coupler', 'YC-2 光纤耦合器', 'yc-2-fiber-coupler',
'2×2 fiber optic coupler for splitting or combining optical signals in spectroscopy systems.',
'2×2光纤耦合器，用于光谱系统中的分光或合光。',
'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600',
NULL,
NULL,
'{"type": "2×2", "coupling_ratio": "50:50", "fiber_core": "9μm", "wavelength_range": "600-1800nm", "connector": "FC/PC"}',
FALSE, 3);

-- Brand Categories
INSERT INTO brand_category (name_en, name_zh, `key`, sort_order) VALUES
('Spectrometers', '光谱仪', 'spectrometer', 1),
('Analyzers', '分析仪', 'analyzer', 2),
('Light Sources', '光源', 'lightsource', 3),
('Sensors', '传感器', 'sensor', 4);

-- Agency Brands
INSERT INTO brand (category_id, name_en, name_zh, slug, logo_url, description_en, description_zh, website_url, featured, sort_order) VALUES
-- Spectrometer brands
(1, 'Thermo Fisher Scientific', '赛默飞世尔科技', 'thermo-fisher',
'https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Thermo_Fisher_Scientific_logo.svg/200px-Thermo_Fisher_Scientific_logo.svg.png',
'Global leader in serving science, helping customers make the world healthier, cleaner, and safer.',
'全球科学服务领域的领导者，帮助客户让世界更健康、更清洁、更安全。',
'https://www.thermofisher.com', TRUE, 1),

(1, 'Agilent Technologies', '安捷伦科技', 'agilent',
'https://upload.wikimedia.org/wikipedia/commons/thumb/4/41/Agilent_Technologies_logo.svg/200px-Agilent_Technologies_logo.svg.png',
'Provider of comprehensive solutions in the life sciences, diagnostics, and applied chemical markets.',
'生命科学、诊断和应用化学市场的综合解决方案供应商。',
'https://www.agilent.com', TRUE, 2),

(1, 'Shimadzu Corporation', '岛津制作所', 'shimadzu',
'https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/Shimadzu_logo.svg/200px-Shimadzu_logo.svg.png',
'Japanese manufacturer specializing in analytical instruments and medical equipment.',
'日本分析仪器和医疗设备制造商。',
'https://www.shimadzu.com', TRUE, 3),

-- Analyzer brands
(2, 'PerkinElmer', '珀金埃尔默', 'perkinelmer',
'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5a/PerkinElmer_logo.svg/200px-PerkinElmer_logo.svg.png',
'Leading technology company focused on human and environmental health.',
'专注于人类和环境健康的领先科技公司。',
'https://www.perkinelmer.com', TRUE, 1),

(2, 'Horiba Scientific', '堀场科学', 'horiba',
'https://upload.wikimedia.org/wikipedia/commons/thumb/8/82/HORIBA_Logo.svg/200px-HORIBA_Logo.svg.png',
'Global company providing a comprehensive range of analytical and measurement equipment.',
'提供全方位分析和测量设备的全球性公司。',
'https://www.horiba.com', FALSE, 2),

-- Light source brands
(3, 'Thorlabs', '索雷博', 'thorlabs',
'https://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Thorlabs_logo.svg/200px-Thorlabs_logo.svg.png',
'Leading designer and manufacturer of photonics equipment including optical components and laser systems.',
'领先的光子设备设计和制造商，包括光学元件和激光系统。',
'https://www.thorlabs.com', TRUE, 1),

(3, 'Hamamatsu Photonics', '滨松光子学', 'hamamatsu',
'https://upload.wikimedia.org/wikipedia/commons/thumb/5/5f/Hamamatsu_Photonics_logo.svg/200px-Hamamatsu_Photonics_logo.svg.png',
'World-leading manufacturer of optical sensors, light sources, and other photonic devices.',
'世界领先的光学传感器、光源和其他光子设备制造商。',
'https://www.hamamatsu.com', FALSE, 2),

-- Sensor brands
(4, 'Baumer Group', '堡盟集团', 'baumer',
'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fc/Baumer_Group_logo.svg/200px-Baumer_Group_logo.svg.png',
'International leading manufacturer of sensors and encoders.',
'国际领先的传感器和编码器制造商。',
'https://www.baumer.com', TRUE, 1),

(4, 'Keyence Corporation', '基恩士', 'keyence',
'https://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Keyence_logo.svg/200px-Keyence_logo.svg.png',
'Japanese manufacturer of sensors, measurement systems, and factory automation equipment.',
'日本传感器、测量系统和工厂自动化设备制造商。',
'https://www.keyence.com', FALSE, 2);

-- News
INSERT INTO news (category, title_en, title_zh, slug, excerpt_en, excerpt_zh, content_en, content_zh, image_url, author, published_at) VALUES
('company', 'SpecSense Expands Global Presence with New European Distribution Center', 'SpecSense拓展全球业务 新设欧洲配送中心',
'specsense-expands-global-presence',
'SpecSense announces the opening of a new European distribution center in Rotterdam, Netherlands to better serve customers across Europe.',
'SpecSense宣布在荷兰鹿特丹设立新的欧洲配送中心，以更好地服务欧洲客户。',
'<p>SpecSense is pleased to announce the opening of our new European distribution center located in Rotterdam, Netherlands. This strategic expansion marks a significant milestone in our global growth strategy and will enable us to provide faster delivery and improved support to our customers throughout Europe.</p><p>The new facility features 2,000 square meters of warehouse space and a dedicated technical support team fluent in multiple European languages. Orders placed by European customers will now be fulfilled within 2-3 business days, significantly reducing lead times compared to previous shipping from our Asian facilities.</p>',
'<p>SpecSense荣幸地宣布，我们在荷兰鹿特丹设立新的欧洲配送中心。这一战略扩张标志着我们全球增长的一个重要里程碑，将使我们能够为欧洲客户提供更快的交付和更好的支持。</p><p>新设施拥有2,000平方米的仓储空间和一支熟练掌握多种欧洲语言的专门技术支持团队。欧洲客户的订单现在将在2-3个工作日内完成，与之前从亚洲设施发货相比，交货时间大大缩短。</p>',
'https://images.unsplash.com/photo-1578575437130-527eed3abbec?w=800',
'SpecSense Marketing',
'2024-06-15 10:00:00'),

('industry', 'The Future of Miniature Spectrometers in Portable Diagnostics', '微型光谱仪在便携诊断领域的未来',
'future-miniature-spectrometers-portable-diagnostics',
'Miniature spectrometers are revolutionizing point-of-care diagnostics with their compact size and increasingly powerful capabilities.',
'微型光谱仪以其紧凑的尺寸和日益强大的功能正在革新即时诊断领域。',
'<p>The field of point-of-care diagnostics is experiencing a paradigm shift with the advent of miniature spectrometers. These compact devices, weighing less than 300 grams, can now perform measurements that previously required benchtop instruments costing tens of thousands of dollars.</p><p>Recent advances in detector technology and miniaturized optical components have made it possible to integrate laboratory-grade spectroscopy into handheld devices. Applications range from blood glucose monitoring to food safety testing and environmental pollutant detection.</p>',
'<p>随着微型光谱仪的出现，即时诊断领域正在经历一场范式转变。这些重量不足300克的紧凑设备，现在可以执行以前只有价值数万美元的台式仪器才能完成的测量。</p><p>探测器技术和微型光学组件的最新进展使得将实验室级光谱集成到手持设备中成为可能。应用范围从血糖监测到食品安全检测和环境污染物检测。</p>',
'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800',
'Dr. Wei Zhang',
'2024-05-20 14:30:00'),

('technology', 'Breakthrough in NIR Spectroscopy for Agricultural Quality Control', '近红外光谱在农业质量控制方面的突破',
'breakthrough-nir-agricultural-quality-control',
'New NIR spectroscopy techniques are enabling rapid, non-destructive analysis of crop quality and nutritional content.',
'新的近红外光谱技术正在实现作物质量和营养含量的快速、无损分析。',
'<p>Agricultural scientists have achieved a significant breakthrough in using NIR spectroscopy for rapid crop quality assessment. The new technique allows farmers and food processors to determine protein content, moisture levels, and contamination in grains within seconds, without any sample preparation.</p><p>The technology utilizes machine learning algorithms trained on thousands of crop samples to achieve accuracy levels comparable to traditional laboratory methods. This advancement is expected to reduce waste and improve food quality standards across the supply chain.</p>',
'<p>农业科学家在使用近红外光谱进行快速作物质量评估方面取得了重大突破。这项新技术允许农民和食品加工商在几秒钟内确定谷物的蛋白质含量、水分含量和污染物，无需任何样品制备。</p><p>该技术利用在数千个作物样本上训练的机器学习算法，达到与传统实验室方法相当的准确性水平。这一进步有望减少浪费并提高整个供应链的食品质量标准。</p>',
'https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800',
'Dr. Li Ming',
'2024-04-10 09:00:00');

-- Applications
INSERT INTO application (`type`, title_en, title_zh, description_en, description_zh, icon, linked_category_keys) VALUES
('industrial', 'Industrial Inspection', '工业检测',
'Advanced spectroscopic solutions for quality control and process monitoring in manufacturing environments.',
'用于制造业环境质量控制和过程监控的先进光谱解决方案。',
'factory',
'spectrometer,analyzer'),

('research', 'Scientific Research', '科学研究',
'High-precision instruments supporting cutting-edge research in chemistry, physics, and materials science.',
'支持化学、物理和材料科学前沿研究的高精度仪器。',
'flask',
'spectrometer,sensor'),

('medical', 'Medical Diagnostics', '医疗诊断',
'Non-invasive spectroscopic tools for clinical diagnostics and biomedical research applications.',
'用于临床诊断和生物医学研究应用的无创光谱工具。',
'heart-pulse',
'analyzer,spectrometer'),

('environment', 'Environmental Monitoring', '环境监测',
'Real-time monitoring solutions for air, water, and soil quality assessment.',
'用于空气、水和土壤质量评估的实时监测解决方案。',
'leaf',
'spectrometer,sensor'),

('agriculture', 'Agriculture', '农业',
'Crop quality analysis and soil nutrient measurement for precision agriculture.',
'精准农业的作物质量分析和土壤养分测量。',
'wheat',
'spectrometer,sensor'),

('food', 'Food & Beverage', '食品与饮料',
'Safety testing and quality control solutions for the food and beverage industry.',
'食品和饮料行业的安全检测和质量控制解决方案。',
'utensils',
'spectrometer,analyzer');

-- Services
INSERT INTO service (`type`, title_en, title_zh, description_en, description_zh) VALUES
('technical', 'Technical Support', '技术支持',
'Our expert technical team provides comprehensive support including installation, calibration, and troubleshooting services.',
'我们的专业技术团队提供包括安装、校准和故障排除在内的全面支持服务。'),

('aftersales', 'After-Sales Service', '售后服务',
'We offer extended warranties, spare parts supply, and regular maintenance services to ensure optimal instrument performance.',
'我们提供延长保修、备件供应和定期维护服务，以确保仪器最佳性能。'),

('downloads', 'Downloads Center', '下载中心',
'Access product manuals, software, datasheets, and catalogs for all SpecSense instruments.',
'访问所有SpecSense仪器的产品手册、软件、数据表和目录。'),

('training', 'Training Services', '培训服务',
'Hands-on training programs conducted by our applications specialists to help you maximize the potential of your instruments.',
'由我们的应用专家提供实践培训课程，帮助您充分发挥仪器的潜力。');

-- Downloads
INSERT INTO download (category, name_en, name_zh, description_en, description_zh, file_url, file_size) VALUES
-- Manuals
('manuals', 'EA-One User Manual', 'EA-One 用户手册', 'Complete operation guide for EA-One UV-Vis Spectrometer', 'EA-One 紫外可见光谱仪完整操作指南', '/downloads/manuals/ea-one-manual.pdf', '4.5MB'),
('manuals', 'NIR-Pro Operation Guide', 'NIR-Pro 操作指南', 'Technical manual for NIR-Pro FT-NIR Spectrometer', 'NIR-Pro 傅立叶近红外光谱仪技术手册', '/downloads/manuals/nir-pro-guide.pdf', '6.2MB'),
('manuals', 'Raman-785 Quick Start', 'Raman-785 快速入门', 'Quick start guide for Raman-785 Portable Raman Spectrometer', 'Raman-785 便携式拉曼光谱仪快速入门指南', '/downloads/manuals/raman-785-qsg.pdf', '2.1MB'),

-- Software
('software', 'SpecSense Pro Software Suite', 'SpecSense Pro 软件套件', 'Data acquisition and analysis software for Windows', 'Windows平台数据采集和分析软件', '/downloads/software/specsense-pro-v3.2.exe', '125MB'),
('software', 'Mobile App for Mini-Spec', 'Mini-Spec 移动应用', 'iOS and Android app for Mini-Spec Pocket Spectrometer', 'iOS和Android版Mini-Spec口袋光谱仪应用', '/downloads/software/mini-spec-app.apk', '45MB'),

-- Datasheets
('datasheets', 'EA-One Datasheet', 'EA-One 数据表', 'Technical specifications and performance data', '技术规格和性能数据', '/downloads/datasheets/ea-one-datasheet.pdf', '1.8MB'),
('datasheets', 'NIR-Pro Datasheet', 'NIR-Pro 数据表', 'Technical specifications and performance data', '技术规格和性能数据', '/downloads/datasheets/nir-pro-datasheet.pdf', '2.3MB'),
('datasheets', 'Raman-785 Datasheet', 'Raman-785 数据表', 'Technical specifications and performance data', '技术规格和性能数据', '/downloads/datasheets/raman-785-datasheet.pdf', '1.5MB'),
('datasheets', 'CS-200 Datasheet', 'CS-200 数据表', 'Color spectrophotometer specifications', '色度分光光度仪规格', '/downloads/datasheets/cs-200-datasheet.pdf', '1.2MB'),

-- Catalogs
('catalogs', 'Full Product Catalog 2024', '2024完整产品目录', 'Complete SpecSense product lineup with specifications', '包含规格的完整SpecSense产品系列', '/downloads/catalogs/specsense-catalog-2024.pdf', '15MB'),
('catalogs', 'Spectrometer Selection Guide', '光谱仪选型指南', 'How to choose the right spectrometer for your application', '如何为您的应用选择合适的光谱仪', '/downloads/catalogs/spectrometer-guide.pdf', '3.5MB'),
('catalogs', 'Accessories Catalog', '配件目录', 'Complete listing of spectrometer accessories and probes', '光谱仪配件和探头完整列表', '/downloads/catalogs/accessories-catalog.pdf', '5.7MB'),
('catalogs', 'Industrial Solutions Brochure', '工业解决方案手册', 'Spectroscopic solutions for industrial applications', '工业应用光谱解决方案', '/downloads/catalogs/industrial-brochure.pdf', '8.2MB');

COMMIT;
