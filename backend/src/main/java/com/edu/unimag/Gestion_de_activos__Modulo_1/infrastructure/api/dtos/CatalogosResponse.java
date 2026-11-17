package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.util.List;

/**
 * Respuesta de GET /api/v1/catalogos.
 */
public record CatalogosResponse(
        List<CatalogoItemResponse> facultades,
        List<CatalogoItemResponse> ubicaciones,
        List<TipoActivoResponse> tiposActivo,
        List<CatalogoItemResponse> estadosDisponibilidad,
        List<CatalogoItemResponse> estadosFisicos
) {
}
