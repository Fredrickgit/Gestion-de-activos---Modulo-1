package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CatalogoItemView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CatalogosView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.TipoActivoCatalogoView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.ConsultarCatalogos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CatalogoItemResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CatalogosResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.TipoActivoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catálogos maestros para formularios del frontend.
 */
@RestController
@RequestMapping("/api/v1/catalogos")
public class CatalogosController {

    private final ConsultarCatalogos consultarCatalogos;

    public CatalogosController(ConsultarCatalogos consultarCatalogos) {
        this.consultarCatalogos = consultarCatalogos;
    }

    @GetMapping
    public CatalogosResponse listar() {
        return toResponse(consultarCatalogos.obtener());
    }

    private CatalogosResponse toResponse(CatalogosView view) {
        return new CatalogosResponse(
                view.facultades().stream().map(this::toItem).toList(),
                view.ubicaciones().stream().map(this::toItem).toList(),
                view.tiposActivo().stream().map(this::toTipo).toList(),
                view.estadosDisponibilidad().stream().map(this::toItem).toList(),
                view.estadosFisicos().stream().map(this::toItem).toList());
    }

    private CatalogoItemResponse toItem(CatalogoItemView view) {
        return new CatalogoItemResponse(view.id(), view.nombre(), view.descripcion());
    }

    private TipoActivoResponse toTipo(TipoActivoCatalogoView view) {
        return new TipoActivoResponse(
                view.id(), view.nombre(), view.descripcion(), view.plazoDevolucion());
    }
}
