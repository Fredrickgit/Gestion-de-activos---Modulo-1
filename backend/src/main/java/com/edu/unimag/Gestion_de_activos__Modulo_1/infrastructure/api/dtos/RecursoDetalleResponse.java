package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.util.List;
import java.util.UUID;

/**
 * Detalle completo de un recurso (activo o espacio).
 */
public record RecursoDetalleResponse(
        UUID id,
        String codigo,
        String nombre,
        String tipo,
        String estadoFisico,
        String estado,
        String ubicacion,
        String facultad,
        Integer plazoDevolucion,
        Integer aforoMaximo,
        List<UUID> equipamiento
) {
}
