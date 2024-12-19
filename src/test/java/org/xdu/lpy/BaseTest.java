package org.xdu.lpy;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class BaseTest {
    
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    
    @BeforeEach
    void setUp() {
        // 清理测试表
        jdbcTemplate.execute("DROP TABLE IF EXISTS excel_meta");
        jdbcTemplate.execute("""
            CREATE TABLE excel_meta (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                file_name VARCHAR(255),
                table_name VARCHAR(255),
                headers TEXT,
                create_time DATETIME,
                update_time DATETIME
            )
        """);
    }
} 