package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;

public record ActualizarEstadoCommand(
        EstadoDisponibilidadRecurso nuevoEstado,
        String motivo,
        String usuarioResponsable
) {
}
