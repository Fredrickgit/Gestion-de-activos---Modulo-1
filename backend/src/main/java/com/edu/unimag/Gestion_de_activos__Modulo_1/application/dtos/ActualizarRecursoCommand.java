package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;

import java.util.List;
import java.util.UUID;

/**
 * Campos editables de un recurso. Los valores nulos conservan el valor actual.
 */
public record ActualizarRecursoCommand(
        String nombre,
        EstadoFisicoActivo estadoFisico,
        Integer plazoDevolucion,
        Integer aforoMaximo,
        UUID ubicacionId,
        UUID facultadId,
        UUID tipoActivoId,
        List<UUID> equipamiento
) {
}
