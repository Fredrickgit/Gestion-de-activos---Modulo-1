package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.UUID;

/**
 * Elemento genérico de catálogo (facultad, ubicación, estado o tipo).
 */
public record CatalogoItemView(
        UUID id,
        String nombre,
        String descripcion
) {
}
