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
