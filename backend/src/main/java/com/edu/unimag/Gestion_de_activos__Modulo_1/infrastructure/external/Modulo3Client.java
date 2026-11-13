package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.dto.NovedadPendienteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

/**
 * Cliente REST para comunicación síncrona con el Módulo 3.
 * Consulta novedades técnicas pendientes sobre recursos.
 */
@Slf4j
@Component
public class Modulo3Client {

    private final RestClient modulo3RestClient;

    public Modulo3Client(@Qualifier("modulo3RestClient") RestClient modulo3RestClient) {
        this.modulo3RestClient = modulo3RestClient;
    }

    /**
     * Consulta si un recurso tiene alguna novedad técnica pendiente en el Módulo 3.
     * GET /api/v1/novedades/pendientes?recursoId={uuid}
     *
     * @param recursoId Identificador único del recurso (UUID v4)
     * @return Optional con la respuesta o empty si hubo timeout/error
     */
    public Optional<NovedadPendienteResponse> consultarNovedadPendiente(UUID recursoId) {
        try {
            log.debug("Consultando novedades pendientes en M3 para recurso: {}", recursoId);
            NovedadPendienteResponse response = modulo3RestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/novedades/pendientes")
                            .queryParam("recursoId", recursoId)
                            .build())
                    .retrieve()
                    .body(NovedadPendienteResponse.class);

            return Optional.ofNullable(response);
        } catch (RestClientException ex) {
            log.error("Error al consultar novedades pendientes en Módulo 3 para recurso {}: {}", recursoId, ex.getMessage());
            return Optional.empty();
        }
    }
}

