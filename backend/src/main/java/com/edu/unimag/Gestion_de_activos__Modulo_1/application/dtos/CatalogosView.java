package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.List;

/**
 * Catálogos maestros necesarios para construir formularios de registro y
 * edición en el frontend.
 */
public record CatalogosView(
        List<CatalogoItemView> facultades,
        List<CatalogoItemView> ubicaciones,
        List<TipoActivoCatalogoView> tiposActivo,
        List<CatalogoItemView> estadosDisponibilidad,
        List<CatalogoItemView> estadosFisicos
) {
}
