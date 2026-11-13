package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.time.OffsetDateTime;

/**
 * Estructura estándar de error definida en specs/REST.md §6.
 */
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
