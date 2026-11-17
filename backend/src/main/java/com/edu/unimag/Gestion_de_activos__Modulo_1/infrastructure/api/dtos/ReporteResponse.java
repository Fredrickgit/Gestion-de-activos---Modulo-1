package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Respuesta paginada de GET /api/v1/reportes.
 */
public record ReporteResponse(
        List<RecursoResumenResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPaginas,
        OffsetDateTime fechaGeneracion
) {
}
