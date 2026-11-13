package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.time.OffsetDateTime;

/**
 * Cuerpo de respuesta para PATCH /api/v1/recursos/{id}/estado.
 */
public record EstadoActualizadoResponse(
        String id,
        String tipo,
        String estadoAnterior,
        String estadoNuevo,
        AuditoriaResponse auditoria
) {
    public EstadoActualizadoResponse(String id, String tipo, String estadoAnterior,
                                     String estadoNuevo, String usuario, OffsetDateTime timestamp) {
        this(id, tipo, estadoAnterior, estadoNuevo, new AuditoriaResponse(usuario, timestamp));
    }
}
