package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la petición PATCH /api/v1/recursos/{id}/estado.
 */
public record ActualizarEstadoRequest(

        @NotNull(message = "El campo 'nuevoEstado' es obligatorio.")
        EstadoDisponibilidadRecurso nuevoEstado,

        @JsonAlias("observacion")
        @Size(max = 255, message = "El campo 'motivo' no puede superar los 255 caracteres.")
        String motivo,

        @NotBlank(message = "El campo 'usuarioResponsable' es obligatorio.")
        @Size(max = 255, message = "El campo 'usuarioResponsable' no puede superar los 255 caracteres.")
        String usuarioResponsable
) {
}
