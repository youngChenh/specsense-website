package com.specsense.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CrmSchemaInitializer {

    private static final Logger log = LoggerFactory.getLogger(CrmSchemaInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        addColumnIfMissing("admin_user", "display_name",
                "ALTER TABLE admin_user ADD COLUMN display_name VARCHAR(100) DEFAULT NULL");
        addColumnIfMissing("admin_user", "status",
                "ALTER TABLE admin_user ADD COLUMN status TINYINT DEFAULT 1");
        addColumnIfMissing("admin_user", "reset_requested",
                "ALTER TABLE admin_user ADD COLUMN reset_requested TINYINT DEFAULT 0");
        jdbcTemplate.execute("UPDATE admin_user SET status = 1 WHERE status IS NULL");
        jdbcTemplate.execute("UPDATE admin_user SET reset_requested = 0 WHERE reset_requested IS NULL");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS crm_customer (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(200) NOT NULL," +
                "country VARCHAR(100)," +
                "website VARCHAR(300)," +
                "industry VARCHAR(100)," +
                "address VARCHAR(500)," +
                "source VARCHAR(50)," +
                "owner_id BIGINT," +
                "status VARCHAR(20) DEFAULT 'active'," +
                "remark TEXT," +
                "attachments TEXT," +
                "closed TINYINT DEFAULT 0," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "INDEX idx_owner (owner_id)," +
                "INDEX idx_country (country)," +
                "INDEX idx_source (source)," +
                "INDEX idx_status (status)," +
                "INDEX idx_name (name)," +
                "INDEX idx_closed (closed)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS crm_contact (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "customer_id BIGINT NOT NULL," +
                "name VARCHAR(100) NOT NULL," +
                "title VARCHAR(100)," +
                "email VARCHAR(200)," +
                "phone VARCHAR(50)," +
                "remark TEXT," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "INDEX idx_customer (customer_id)," +
                "INDEX idx_email (email)," +
                "INDEX idx_phone (phone)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS crm_inquiry (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "customer_id BIGINT NOT NULL," +
                "contact_id BIGINT," +
                "inquiry_date DATE," +
                "source VARCHAR(50)," +
                "requirement TEXT," +
                "stage VARCHAR(30) DEFAULT 'NEW'," +
                "owner_id BIGINT," +
                "next_follow_date DATE," +
                "remark TEXT," +
                "attachments TEXT," +
                "closed TINYINT DEFAULT 0," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "INDEX idx_customer (customer_id)," +
                "INDEX idx_owner (owner_id)," +
                "INDEX idx_stage (stage)," +
                "INDEX idx_source (source)," +
                "INDEX idx_closed (closed)," +
                "INDEX idx_inquiry_date (inquiry_date)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        addColumnIfMissing("crm_inquiry", "website_inquiry_id",
                "ALTER TABLE crm_inquiry ADD COLUMN website_inquiry_id BIGINT DEFAULT NULL");

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS crm_follow_up (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "inquiry_id BIGINT," +
                "customer_id BIGINT NOT NULL," +
                "follow_date DATE," +
                "method VARCHAR(30)," +
                "content TEXT," +
                "result TEXT," +
                "attachments TEXT," +
                "created_by BIGINT," +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "INDEX idx_inquiry (inquiry_id)," +
                "INDEX idx_customer (customer_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        log.info("CRM schema ensured");
    }

    private void addColumnIfMissing(String table, String column, String ddl) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class, table, column);
        if (count == null || count == 0) {
            jdbcTemplate.execute(ddl);
        }
    }
}
