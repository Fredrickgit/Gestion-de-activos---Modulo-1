package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Cuerpo de la petición POST /api/v1/activos.
 *
 * <p>{@code ubicacionId} referencia al espacio físico al que pertenece el activo
 * (columna {@code activo.espacio_id}). {@code facultadId} es opcional y, cuando se
 * envía, debe coincidir con la facultad del espacio.</p>
 */
public record CrearActivoRequest(

        @NotBlank(message = "El campo 'nombre' es obligatorio.")
        @Size(max = 255, message = "El campo 'nombre' no puede superar los 255 caracteres.")
        String nombre,

        @NotBlank(message = "El campo 'codigo' es obligatorio.")
        @Size(max = 100, message = "El campo 'codigo' no puede superar los 100 caracteres.")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$",
                message = "El campo 'codigo' solo admite letras, números, puntos, guiones y guiones bajos.")
        String codigo,

        @NotNull(message = "El campo 'estadoFisico' es obligatorio.")
        EstadoFisicoActivo estadoFisico,

        @NotNull(message = "El campo 'ubicacionId' es obligatorio.")
        UUID ubicacionId,

        UUID facultadId,

        UUID tipoActivoId,

        @PositiveOrZero(message = "El campo 'plazoDevolucion' debe ser mayor o igual a cero.")
        Integer plazoDevolucion
) {
}
