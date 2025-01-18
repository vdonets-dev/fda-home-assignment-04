package com.fda.home;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
@Transactional
public abstract class BaseRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:15.2")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void setupProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

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
