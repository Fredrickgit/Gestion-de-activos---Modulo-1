package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Movimiento del historial de un recurso.
 */
public record HistorialRecursoResponse(
        UUID id,
        UUID recursoId,
        String estadoAnterior,
        String estadoNuevo,
        String usuarioResponsable,
        LocalDateTime fechaEvento,
        String motivo
) {
}
