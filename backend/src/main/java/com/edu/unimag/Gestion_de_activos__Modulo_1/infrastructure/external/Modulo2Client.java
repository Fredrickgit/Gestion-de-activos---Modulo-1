package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.dto.ReservaActivaResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

/**
 * Cliente REST para comunicación síncrona con el Módulo 2.
 * Consulta reservas activas sobre recursos.
 */
@Slf4j
@Component
public class Modulo2Client {

    private final RestClient modulo2RestClient;

    public Modulo2Client(@Qualifier("modulo2RestClient") RestClient modulo2RestClient) {
        this.modulo2RestClient = modulo2RestClient;
    }

    /**
     * Consulta si un recurso tiene alguna reserva activa en el Módulo 2.
     * GET /api/v1/reservas/activas?recursoId={uuid}
     *
     * @param recursoId Identificador único del recurso (UUID v4)
     * @return Optional con la respuesta o empty si hubo timeout/error
     */
    public Optional<ReservaActivaResponse> consultarReservaActiva(UUID recursoId) {
        try {
            log.debug("Consultando reservas activas en M2 para recurso: {}", recursoId);
            ReservaActivaResponse response = modulo2RestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/reservas/activas")
                            .queryParam("recursoId", recursoId)
                            .build())
                    .retrieve()
                    .body(ReservaActivaResponse.class);

            return Optional.ofNullable(response);
        } catch (RestClientException ex) {
            log.error("Error al consultar reservas activas en Módulo 2 para recurso {}: {}", recursoId, ex.getMessage());
            return Optional.empty();
        }
    }
}

