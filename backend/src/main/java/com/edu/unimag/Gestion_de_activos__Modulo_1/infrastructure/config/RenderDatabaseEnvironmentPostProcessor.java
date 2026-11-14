package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Traduce la variable {@code DATABASE_URL} que inyecta Render
 * ({@code postgresql://usuario:clave@host:puerto/base}) al formato JDBC que
 * espera Spring Boot ({@code jdbc:postgresql://host:puerto/base}).
 *
 * <p>El driver de PostgreSQL no acepta el esquema {@code postgresql://} de
 * Render, por eso no basta con apuntar {@code spring.datasource.url} a
 * {@code ${DATABASE_URL}} directamente.</p>
 *
 * <p>Si existe {@code DB_URL} (ya en formato JDBC) se respeta esa variable y
 * este procesador no hace nada. Lo mismo aplica a {@code DB_USERNAME} y
 * {@code DB_PASSWORD}, que tienen prioridad sobre las credenciales embebidas
 * en la URL.</p>
 */
public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String DATABASE_URL = "DATABASE_URL";
    private static final String DB_URL = "DB_URL";
    private static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    private static final String SPRING_DATASOURCE_USERNAME = "spring.datasource.username";
    private static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";
    private static final String POSTGRES_SCHEME = "postgres://";
    private static final String POSTGRESQL_SCHEME = "postgresql://";
    private static final int POSTGRES_DEFAULT_PORT = 5432;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = environment.getProperty(DATABASE_URL);
        if (!StringUtils.hasText(databaseUrl) || StringUtils.hasText(environment.getProperty(DB_URL))) {
            return;
        }
        if (!databaseUrl.startsWith(POSTGRES_SCHEME) && !databaseUrl.startsWith(POSTGRESQL_SCHEME)) {
            // No es una URL de Postgres: se deja que Spring resuelva la configuracion.
            return;
        }

        URI uri = parse(databaseUrl);
        Map<String, Object> properties = new HashMap<>();
        properties.put(SPRING_DATASOURCE_URL, buildJdbcUrl(uri));

        String[] credentials = extractCredentials(uri);
        if (credentials != null) {
            if (!StringUtils.hasText(environment.getProperty("DB_USERNAME"))) {
                properties.put(SPRING_DATASOURCE_USERNAME, credentials[0]);
            }
            if (!StringUtils.hasText(environment.getProperty("DB_PASSWORD"))) {
                properties.put(SPRING_DATASOURCE_PASSWORD, credentials[1]);
            }
        }

        // addFirst garantiza que estos valores ganen sobre application.yaml,
        // pero no sobre variables de entorno con nombre explicito ya definidas.
        environment.getPropertySources().addFirst(
                new MapPropertySource("renderDatabaseUrl", properties));
    }

    private URI parse(String databaseUrl) {
        String normalized = databaseUrl.startsWith(POSTGRES_SCHEME)
                ? POSTGRESQL_SCHEME + databaseUrl.substring(POSTGRES_SCHEME.length())
                : databaseUrl;
        try {
            return new URI(normalized);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(
                    "DATABASE_URL no tiene un formato valido: " + ex.getMessage(), ex);
        }
    }

    private String buildJdbcUrl(URI uri) {
        String host = uri.getHost();
        if (!StringUtils.hasText(host)) {
            throw new IllegalArgumentException("DATABASE_URL no incluye un host valido");
        }
        int port = uri.getPort() == -1 ? POSTGRES_DEFAULT_PORT : uri.getPort();
        String path = StringUtils.hasText(uri.getRawPath()) ? uri.getRawPath() : "";
        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(host)
                .append(':')
                .append(port)
                .append(path);
        if (StringUtils.hasText(uri.getRawQuery())) {
            jdbcUrl.append('?').append(uri.getRawQuery());
        }
        return jdbcUrl.toString();
    }

    private String[] extractCredentials(URI uri) {
        String userInfo = uri.getUserInfo();
        if (!StringUtils.hasText(userInfo)) {
            return null;
        }
        int separator = userInfo.indexOf(':');
        if (separator < 0) {
            return new String[]{userInfo, ""};
        }
        return new String[]{
                userInfo.substring(0, separator),
                userInfo.substring(separator + 1)
        };
    }

    @Override
    public int getOrder() {
        // Se ejecuta despues de que Spring Boot haya cargado application.yaml.
        return Ordered.LOWEST_PRECEDENCE;
    }
}
