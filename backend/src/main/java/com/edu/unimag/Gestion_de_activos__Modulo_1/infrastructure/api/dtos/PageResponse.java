package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.util.List;

/**
 * Envoltura de paginación usada por GET /api/v1/recursos.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements
) {
}
