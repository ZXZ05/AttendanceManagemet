package com.zpark.sb.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaCompatibilityInitializer {

    private static final Logger log = LoggerFactory.getLogger(SchemaCompatibilityInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public SchemaCompatibilityInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void ensureEmployeeAvatarColumn() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'employee' AND COLUMN_NAME = 'avatar'",
                    Integer.class
            );
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE employee ADD COLUMN avatar varchar(500) NULL");
                log.warn("Added missing column employee.avatar for schema compatibility.");
            }
        } catch (Exception ex) {
            log.warn("Failed to validate employee.avatar column. Please check database schema manually.", ex);
        }
    }
}
