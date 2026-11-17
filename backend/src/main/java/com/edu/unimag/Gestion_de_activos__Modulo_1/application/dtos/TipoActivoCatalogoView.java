package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.UUID;

/**
 * Tipo de activo con su plazo de devolución sugerido.
 */
public record TipoActivoCatalogoView(
        UUID id,
        String nombre,
        String descripcion,
        int plazoDevolucion
) {
}
