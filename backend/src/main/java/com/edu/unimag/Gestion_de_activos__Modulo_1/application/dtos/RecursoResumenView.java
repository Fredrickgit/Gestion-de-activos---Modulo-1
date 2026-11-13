package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.UUID;

public record RecursoResumenView(
        UUID id,
        String codigo,
        String nombre,
        String tipo,
        String estado,
        String estadoFisico,
        String facultad,
        String ubicacion,
        Integer aforoMaximo,
        Integer plazoDevolucion
) {
}
