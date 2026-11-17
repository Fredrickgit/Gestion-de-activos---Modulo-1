package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Cuerpo de PATCH /api/v1/recursos/{id}. Los campos omitidos no se modifican.
 */
public record ActualizarRecursoRequest(
        @Size(max = 255, message = "El campo 'nombre' no puede superar los 255 caracteres.")
        String nombre,

        EstadoFisicoActivo estadoFisico,

        @PositiveOrZero(message = "El campo 'plazoDevolucion' debe ser mayor o igual a cero.")
        Integer plazoDevolucion,

        @Positive(message = "El campo 'aforoMaximo' debe ser un número positivo.")
        Integer aforoMaximo,

        UUID ubicacionId,

        UUID facultadId,

        UUID tipoActivoId,

        List<UUID> equipamiento
) {
}
