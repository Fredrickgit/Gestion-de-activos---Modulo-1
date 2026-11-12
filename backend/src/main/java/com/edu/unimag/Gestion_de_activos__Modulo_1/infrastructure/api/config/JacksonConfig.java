package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Objeto Jackson 2 usado por los consumidores/lectores de la infraestructura.
 *
 * <p>Spring Boot 4 auto-configura Jackson 3 para la capa HTTP, mientras que
 * Spring Kafka y los DTOs existentes del proyecto usan Jackson 2. Este bean
 * mantiene disponible el {@code com.fasterxml.jackson.databind.ObjectMapper}
 * con el módulo de fechas Java 8 registrado.</p>
 */
@Configuration
public class JacksonConfig {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}
