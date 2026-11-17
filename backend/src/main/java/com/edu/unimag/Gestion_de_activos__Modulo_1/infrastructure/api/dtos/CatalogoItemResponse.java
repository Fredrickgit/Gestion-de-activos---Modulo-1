package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.util.UUID;

/**
 * Elemento de catálogo expuesto al frontend.
 */
public record CatalogoItemResponse(
        UUID id,
        String nombre,
        String descripcion
) {
}
