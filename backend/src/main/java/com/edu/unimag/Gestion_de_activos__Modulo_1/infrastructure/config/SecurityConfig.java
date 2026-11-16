package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.config;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Configuración de seguridad de la API (specs/REST.md §6).
 *
 * <p>La API es REST con autenticación HTTP Basic, por lo que:</p>
 * <ul>
 *   <li>no hay sesión ni formulario de login ({@code STATELESS});</li>
 *   <li>CSRF está deshabilitado: la cadena por defecto de Spring Boot lo evalúa
 *       antes de Basic Auth y rechaza cualquier POST/PATCH con 401;</li>
 *   <li>las consultas ({@code GET}) requieren autenticación;</li>
 *   <li>crear activos/espacios y cambiar el estado exige rol
 *       {@code DIRECCION} (Dirección Universitaria);</li>
 *   <li>los errores 401/403 se devuelven con el mismo JSON de {@link ErrorResponse}
 *       que el resto de la API.</li>
 * </ul>
 *
 * <p>Las credenciales se configuran por variables de entorno. El usuario
 * principal ({@code SECURITY_USER}/{@code SECURITY_PASSWORD}) tiene el rol
 * {@code DIRECCION}. De forma opcional se puede habilitar un usuario de solo
 * lectura (rol {@code MONITOR}) con
 * {@code SECURITY_MONITOR_USER}/{@code SECURITY_MONITOR_PASSWORD}.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String ROLE_DIRECCION = "DIRECCION";
    public static final String ROLE_MONITOR = "MONITOR";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   ObjectMapper objectMapper) throws Exception {
        AuthenticationEntryPoint entryPoint =
                (request, response, ex) -> escribirError(response, objectMapper, request,
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales de autenticación ausentes o inválidas en la cabecera de la solicitud.");

        AccessDeniedHandler accessDeniedHandler =
                (request, response, ex) -> escribirError(response, objectMapper, request,
                        HttpStatus.FORBIDDEN,
                        "El rol actual no cuenta con privilegios de Dirección Universitaria para esta operación.");

        http
                // API sin estado: no hay cookie de sesión ni token CSRF.
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Consultas: cualquier usuario autenticado (Dirección o Monitores).
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").authenticated()
                        // Registro de recursos: exclusivo de Dirección Universitaria.
                        .requestMatchers(HttpMethod.POST, "/api/v1/activos", "/api/v1/espacios")
                        .hasAnyRole(ROLE_DIRECCION)
                        // Actualización manual de estado: exclusivo de Dirección Universitaria.
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/recursos/*/estado")
                        .hasAnyRole(ROLE_DIRECCION)
                        // Cualquier otra ruta de la API requiere autenticación.
                        .requestMatchers("/api/v1/**").authenticated()
                        // El reenvío interno de errores de Spring no debe
                        // convertirse en 401: el cuerpo real lo genera
                        // GlobalExceptionHandler.
                        .requestMatchers("/error").permitAll()
                        .anyRequest().denyAll())
                .httpBasic(basic -> basic.authenticationEntryPoint(entryPoint))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,
            @Value("${app.security.user:admin}") String username,
            @Value("${app.security.password:admin}") String password,
            @Value("${app.security.monitor-user:}") String monitorUsername,
            @Value("${app.security.monitor-password:}") String monitorPassword) {

        UserDetails direccion = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles(ROLE_DIRECCION)
                .build();

        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(direccion);

        if (!monitorUsername.isBlank() && !monitorPassword.isBlank()) {
            UserDetails monitor = User.withUsername(monitorUsername)
                    .password(passwordEncoder.encode(monitorPassword))
                    .roles(ROLE_MONITOR)
                    .build();
            manager.createUser(monitor);
        }

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS para que el frontend pueda consumir la API con Basic Auth.
     * Los orígenes se limitan con {@code CORS_ALLOWED_ORIGINS} (separados por
     * coma); por defecto {@code *} para facilitar el despliegue inicial.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.security.cors-allowed-origins:*}") String allowedOrigins) {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                java.util.Arrays.stream(allowedOrigins.split(","))
                        .map(String::trim)
                        .filter(origin -> !origin.isEmpty())
                        .toList());
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type", "X-Correlation-Id"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/**", configuration);
        return source;
    }

    private static void escribirError(HttpServletResponse response,
                                      ObjectMapper objectMapper,
                                      HttpServletRequest request,
                                      HttpStatus status,
                                      String message) throws IOException {
        ErrorResponse body = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
