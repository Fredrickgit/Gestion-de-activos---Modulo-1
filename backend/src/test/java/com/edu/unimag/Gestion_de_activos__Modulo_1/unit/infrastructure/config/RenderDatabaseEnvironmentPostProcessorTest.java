package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.config;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.config.RenderDatabaseEnvironmentPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RenderDatabaseEnvironmentPostProcessorTest {

    private final RenderDatabaseEnvironmentPostProcessor processor =
            new RenderDatabaseEnvironmentPostProcessor();

    @Test
    void convierteLaUrlPostgresqlDeRenderAFormatoJdbc() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DATABASE_URL", "postgresql://gestion:secreta@dpg-abc-a.oregon-postgres.render.com:5432/gestion_activos_modulo1");

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertThat(environment.getProperty("spring.datasource.url"))
                .isEqualTo("jdbc:postgresql://dpg-abc-a.oregon-postgres.render.com:5432/gestion_activos_modulo1");
        assertThat(environment.getProperty("spring.datasource.username")).isEqualTo("gestion");
        assertThat(environment.getProperty("spring.datasource.password")).isEqualTo("secreta");
    }

    @Test
    void aceptaElEsquemaPostgresCortoYPuertoPorDefecto() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DATABASE_URL", "postgres://usuario:clave@localhost/base");

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertThat(environment.getProperty("spring.datasource.url"))
                .isEqualTo("jdbc:postgresql://localhost:5432/base");
    }

    @Test
    void respetaDbUrlExplicitaEnFormatoJdbc() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DATABASE_URL", "postgresql://usuario:clave@host:5432/base")
                .withProperty("DB_URL", "jdbc:postgresql://otro-host:5433/otra-base");

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertThat(environment.getProperty("spring.datasource.url")).isNull();
    }

    @Test
    void noHaceNadaSiNoExisteDatabaseUrl() {
        MockEnvironment environment = new MockEnvironment();

        processor.postProcessEnvironment(environment, new SpringApplication());

        assertThat(environment.getProperty("spring.datasource.url")).isNull();
    }

    @Test
    void fallaConMensajeClaroSiLaUrlEsInvalida() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DATABASE_URL", "postgresql://usuario:clave@host con espacios:5432/base");

        assertThatThrownBy(() -> processor.postProcessEnvironment(environment, new SpringApplication()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DATABASE_URL");
    }
}
