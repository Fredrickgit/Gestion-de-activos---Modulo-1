package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.util.UUID;

/**
 * Tipo de activo expuesto al frontend.
 */
public record TipoActivoResponse(
        UUID id,
        String nombre,
        String descripcion,
        int plazoDevolucion
) {
}
