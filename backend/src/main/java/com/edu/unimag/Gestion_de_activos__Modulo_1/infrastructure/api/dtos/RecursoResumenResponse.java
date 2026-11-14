package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Fila del listado paginado de recursos (activos y espacios).
 */
public record RecursoResumenResponse(
        UUID id,
        String codigo,
        String nombre,
        String tipo,
        String estado,
        String estadoFisico,
        String facultad,
        String ubicacion,
        Integer aforoMaximo,
        Integer plazoDevolucion,
        LocalDateTime fechaRegistro
) {
}
