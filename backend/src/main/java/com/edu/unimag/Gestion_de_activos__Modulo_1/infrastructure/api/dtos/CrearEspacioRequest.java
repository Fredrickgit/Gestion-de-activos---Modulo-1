package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Cuerpo de la petición POST /api/v1/espacios.
 *
 * <p>{@code ubicacionId} identifica la ubicación universitaria (bloque, hangar,
 * edificio). Si no se envía, se usa {@code edificio} para buscar o crear dicha
 * ubicación, manteniendo compatibilidad con el contrato de REST.md.</p>
 */
public record CrearEspacioRequest(

        @NotBlank(message = "El campo 'nombre' es obligatorio.")
        @Size(max = 255, message = "El campo 'nombre' no puede superar los 255 caracteres.")
        String nombre,

        @Size(max = 100, message = "El campo 'codigo' no puede superar los 100 caracteres.")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$",
                message = "El campo 'codigo' solo admite letras, números, puntos, guiones y guiones bajos.")
        String codigo,

        @NotNull(message = "El campo 'aforoMaximo' es obligatorio.")
        @Positive(message = "El campo 'aforoMaximo' debe ser un número positivo.")
        Integer aforoMaximo,

        @NotNull(message = "El campo 'facultadId' es obligatorio.")
        UUID facultadId,

        UUID ubicacionId,

        @Size(max = 255, message = "El campo 'edificio' no puede superar los 255 caracteres.")
        String edificio,

        List<@NotNull(message = "Cada elemento de 'equipamiento' debe ser un UUID válido.") UUID> equipamiento,

        @PositiveOrZero(message = "El campo 'plazoDevolucion' debe ser mayor o igual a cero.")
        Integer plazoDevolucion
) {
}
