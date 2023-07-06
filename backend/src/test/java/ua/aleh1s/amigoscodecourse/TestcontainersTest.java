package ua.aleh1s.amigoscodecourse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestcontainersTest extends BaseTestcontainers {

    @Test
    void canStartPostgresDB() {
        assertThat(postgresDBContainer.isRunning()).isTrue();
        assertThat(postgresDBContainer.isCreated()).isTrue();
    }
}
