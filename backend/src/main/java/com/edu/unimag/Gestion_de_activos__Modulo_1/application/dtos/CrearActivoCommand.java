package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;

import java.util.UUID;

public record CrearActivoCommand(
        String nombre,
        String codigo,
        EstadoFisicoActivo estadoFisico,
        UUID ubicacionId,
        UUID facultadId,
        UUID tipoActivoId,
        Integer plazoDevolucion
) {
}
