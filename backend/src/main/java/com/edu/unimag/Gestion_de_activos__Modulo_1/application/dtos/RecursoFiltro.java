package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.UUID;

/**
 * Criterios de búsqueda del catálogo y de los reportes de inventario.
 */
public record RecursoFiltro(
        String tipo,
        String estado,
        UUID facultadId,
        String facultadNombre,
        String nombre,
        Integer aforoMinimo
) {
    public static RecursoFiltro vacio() {
        return new RecursoFiltro(null, null, null, null, null, null);
    }
}
