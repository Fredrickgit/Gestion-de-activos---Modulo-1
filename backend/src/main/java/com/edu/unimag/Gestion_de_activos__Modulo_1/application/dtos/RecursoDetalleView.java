package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.List;
import java.util.UUID;

public record RecursoDetalleView(
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
