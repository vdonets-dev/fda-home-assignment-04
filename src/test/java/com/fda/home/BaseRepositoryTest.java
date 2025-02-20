package com.fda.home;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public abstract class BaseRepositoryTest extends BaseTestContainer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @SneakyThrows
    @Transactional
    void cleanDatabase() {
        try {
            jdbcTemplate.execute("TRUNCATE TABLE drug_application_product CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE drug_application_substance CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE drug_application_manufacturer CASCADE");
            jdbcTemplate.execute("TRUNCATE TABLE drug_application CASCADE");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to clean database before test execution", e);
        }
    }

}
