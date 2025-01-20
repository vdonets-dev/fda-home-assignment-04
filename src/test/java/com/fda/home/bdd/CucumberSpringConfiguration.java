package com.fda.home.bdd;

import com.fda.home.BaseTestContainer;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class CucumberSpringConfiguration extends BaseTestContainer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    @Transactional
    public void cleanDatabase() {
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
