package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoFiltro;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.ReporteResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.schemas.RecursoSchemas;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Reporte paginado de inventario (SPEC-HISTORIA4).
 */
@RestController
@RequestMapping("/api/v1/reportes")
@Validated
public class ReportesController {

    private final GestionRecursos gestionRecursos;
    private final RecursoApiMapper mapper;

    public ReportesController(GestionRecursos gestionRecursos, RecursoApiMapper mapper) {
        this.gestionRecursos = gestionRecursos;
        this.mapper = mapper;
    }

    @GetMapping
    public ReporteResponse generar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String facultad,
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "nombre") String orden,
            @RequestParam(defaultValue = "asc") String direccion,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "El parámetro 'page' debe ser mayor o igual a cero.") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "El parámetro 'size' debe ser mayor o igual a 1.")
            @Max(value = 20, message = "El parámetro 'size' no puede ser mayor a 20.") int size) {

        RecursoSchemas.FacultadCriteria facultadCriteria = RecursoSchemas.parseFacultad(facultad);
        RecursoFiltro filtro = new RecursoFiltro(
                RecursoSchemas.parseTipo(tipo),
                RecursoSchemas.parseEstado(estado),
                facultadCriteria.id(),
                facultadCriteria.nombre(),
                nombre,
                null);

        var reporte = gestionRecursos.generarReporte(filtro, orden, direccion, page, size);
        List<RecursoResumenResponse> content = reporte.content().stream()
                .map(mapper::toResumenPublico)
                .toList();

        return new ReporteResponse(
                content,
                reporte.page(),
                reporte.size(),
                reporte.totalElements(),
                reporte.totalPaginas(),
                reporte.fechaGeneracion());
    }
}
