package ua.aleh1s.amigoscodecourse;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class BaseTestcontainers {

    protected static final Faker FAKER = new Faker();

    @BeforeAll
    static void beforeAll() {
        Flyway.configure().dataSource(
                postgresDBContainer.getJdbcUrl(),
                postgresDBContainer.getUsername(),
                postgresDBContainer.getPassword()
        ).load().migrate();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgresDBContainer
            = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("amigoscode_test_db")
            .withUsername("username")
            .withPassword("password");

    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresDBContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresDBContainer::getUsername);
        registry.add("spring.datasource.password", postgresDBContainer::getPassword);
    }

    protected static DataSource getDataSource() {
        return DataSourceBuilder.create()
                .url(postgresDBContainer.getJdbcUrl())
                .username(postgresDBContainer.getUsername())
                .password(postgresDBContainer.getPassword())
                .driverClassName(postgresDBContainer.getDriverClassName())
                .build();
    }
}
