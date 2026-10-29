-- SpeSense CRM v1 schema (idempotent companion to specsense_db.sql)

ALTER TABLE admin_user
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(100) DEFAULT NULL COMMENT '显示名称',
    ADD COLUMN IF NOT EXISTS status TINYINT DEFAULT 1 COMMENT '1启用 0停用',
    ADD COLUMN IF NOT EXISTS reset_requested TINYINT DEFAULT 0 COMMENT '1已申请重置密码';

CREATE TABLE IF NOT EXISTS crm_customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '客户名称',
    country VARCHAR(100) COMMENT '国家/地区',
    website VARCHAR(300) COMMENT '官网',
    industry VARCHAR(100) COMMENT '行业',
    address VARCHAR(500) COMMENT '地址',
    source VARCHAR(50) COMMENT '线索来源: website/exhibition/outbound/existing/social',
    owner_id BIGINT COMMENT '所属销售',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态: potential/active/closed',
    remark TEXT COMMENT '备注',
    attachments TEXT COMMENT '附件JSON',
    closed TINYINT DEFAULT 0 COMMENT '1已关闭',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_owner (owner_id),
    INDEX idx_country (country),
    INDEX idx_source (source),
    INDEX idx_status (status),
    INDEX idx_name (name),
    INDEX idx_closed (closed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM客户';

CREATE TABLE IF NOT EXISTS crm_contact (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '联系人姓名',
    title VARCHAR(100) COMMENT '职位',
    email VARCHAR(200) COMMENT '邮箱',
    phone VARCHAR(50) COMMENT '电话/手机',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer (customer_id),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM联系人';

CREATE TABLE IF NOT EXISTS crm_inquiry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    contact_id BIGINT COMMENT '关联联系人',
    inquiry_date DATE COMMENT '询盘日期',
    source VARCHAR(50) COMMENT '线索来源',
    requirement TEXT COMMENT '客户需求',
    stage VARCHAR(30) DEFAULT 'NEW' COMMENT 'NEW/FOLLOWING/QUOTED/NEGOTIATING/WON/LOST',
    owner_id BIGINT COMMENT '所属销售',
    next_follow_date DATE COMMENT '下一次跟进日期',
    remark TEXT COMMENT '备注',
    attachments TEXT COMMENT '附件JSON',
    closed TINYINT DEFAULT 0 COMMENT '1已关闭',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer (customer_id),
    INDEX idx_owner (owner_id),
    INDEX idx_stage (stage),
    INDEX idx_source (source),
    INDEX idx_closed (closed),
    INDEX idx_inquiry_date (inquiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM询盘';

ALTER TABLE crm_inquiry
    ADD COLUMN IF NOT EXISTS website_inquiry_id BIGINT DEFAULT NULL COMMENT '官网留言ID';

CREATE TABLE IF NOT EXISTS crm_follow_up (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inquiry_id BIGINT COMMENT '关联询盘',
    customer_id BIGINT NOT NULL,
    follow_date DATE COMMENT '跟进日期',
    method VARCHAR(30) COMMENT 'email/phone/whatsapp/meeting/other',
    content TEXT COMMENT '沟通内容',
    result TEXT COMMENT '跟进结果',
    attachments TEXT COMMENT '附件JSON',
    created_by BIGINT COMMENT '记录人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_inquiry (inquiry_id),
    INDEX idx_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM跟进记录';

CREATE TABLE IF NOT EXISTS crm_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT COMMENT '客户',
    inquiry_id BIGINT COMMENT '关联询盘',
    owner_id BIGINT COMMENT '所属销售',
    order_no VARCHAR(50) COMMENT '订单号',
    order_date DATE COMMENT '订单日期',
    supplier_name VARCHAR(200) COMMENT '供应商',
    product_name VARCHAR(200) COMMENT '产品名称',
    model VARCHAR(200) COMMENT '型号',
    quantity DECIMAL(12,2) DEFAULT 1,
    currency VARCHAR(10) DEFAULT 'USD',
    purchase_price DECIMAL(14,2) DEFAULT 0 COMMENT '采购价',
    sell_price DECIMAL(14,2) DEFAULT 0 COMMENT '卖出价',
    incoterm VARCHAR(10) COMMENT 'EXW/FOB/CFR/CIF',
    freight DECIMAL(14,2) DEFAULT 0 COMMENT '运费',
    insurance DECIMAL(14,2) DEFAULT 0 COMMENT '保险费',
    bank_fee DECIMAL(14,2) DEFAULT 0 COMMENT '银行手续费',
    tax_included TINYINT DEFAULT 0 COMMENT '1含税 0不含税',
    invoice_type VARCHAR(20) COMMENT 'normal普票 special专票',
    gross_profit DECIMAL(14,2) DEFAULT 0 COMMENT '毛利',
    gross_margin DECIMAL(8,2) DEFAULT 0 COMMENT '毛利率%',
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer (customer_id),
    INDEX idx_owner (owner_id),
    INDEX idx_supplier (supplier_name),
    INDEX idx_order_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM订单';

CREATE TABLE IF NOT EXISTS crm_after_sale (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT,
    order_id BIGINT,
    owner_id BIGINT,
    supplier_name VARCHAR(200) COMMENT '供应商名称',
    model VARCHAR(200) COMMENT '具体型号',
    problem TEXT COMMENT '出现的具体问题',
    resolved TINYINT DEFAULT 0 COMMENT '1已解决',
    resolved_at DATE,
    need_change_supplier TINYINT DEFAULT 0 COMMENT '是否需要更换供应商',
    remark TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer (customer_id),
    INDEX idx_owner (owner_id),
    INDEX idx_supplier (supplier_name),
    INDEX idx_model (model),
    INDEX idx_resolved (resolved)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='CRM售后问题';
