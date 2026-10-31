package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.connection;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion del pool de conexiones a PostgreSQL.
 *
 * <p>Cuando existe un {@link JdbcConnectionDetails} (por ejemplo, el que genera
 * Testcontainers con {@code @ServiceConnection}), sus valores tienen prioridad
 * sobre las propiedades estaticas de {@code application.yaml}. De esta forma la
 * infraestructura funciona igual en produccion y en pruebas de integracion.</p>
 *
 * <p>Las migraciones Flyway se ubican en
 * {@code classpath:infrastructure/database/connection/migrations} y son
 * ejecutadas por la auto-configuracion de Spring Boot antes de que Hibernate
 * valide el esquema ({@code ddl-auto: validate}).</p>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariDataSource dataSource(
            DataSourceProperties properties,
            ObjectProvider<JdbcConnectionDetails> connectionDetailsProvider) {

        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        JdbcConnectionDetails connectionDetails = connectionDetailsProvider.getIfAvailable();
        if (connectionDetails != null) {
            dataSource.setJdbcUrl(connectionDetails.getJdbcUrl());
            dataSource.setUsername(connectionDetails.getUsername());
            dataSource.setPassword(connectionDetails.getPassword());
            if (connectionDetails.getDriverClassName() != null) {
                dataSource.setDriverClassName(connectionDetails.getDriverClassName());
            }
        }

        return dataSource;
    }
}
