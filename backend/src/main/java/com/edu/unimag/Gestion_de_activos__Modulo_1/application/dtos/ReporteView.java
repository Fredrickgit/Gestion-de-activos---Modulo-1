package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Vista previa paginada de un reporte de inventario.
 */
public record ReporteView(
        List<RecursoResumenView> content,
        int page,
        int size,
        long totalElements,
        int totalPaginas,
        OffsetDateTime fechaGeneracion
) {
}
