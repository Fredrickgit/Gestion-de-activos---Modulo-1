package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.ActualizarEstadoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.ActualizarEstadoRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.DisponibilidadResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.EstadoActualizadoResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.PageResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoResumenResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.schemas.RecursoSchemas;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Endpoints de catálogo, detalle, disponibilidad y actualización manual de estado.
 */
@RestController
@RequestMapping("/api/v1/recursos")
@Validated
public class RecursosController {

    private final GestionRecursos gestionRecursos;
    private final RecursoApiMapper mapper;

    public RecursosController(GestionRecursos gestionRecursos, RecursoApiMapper mapper) {
        this.gestionRecursos = gestionRecursos;
        this.mapper = mapper;
    }

    @GetMapping
    public PageResponse<RecursoResumenResponse> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String facultad,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "El parámetro 'page' debe ser mayor o igual a cero.") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "El parámetro 'size' debe ser mayor o igual a 1.")
            @Max(value = 20, message = "El parámetro 'size' no puede ser mayor a 20.") int size) {

        RecursoSchemas.FacultadCriteria facultadCriteria = RecursoSchemas.parseFacultad(facultad);

        return mapper.toPage(gestionRecursos.listar(
                RecursoSchemas.parseTipo(tipo),
                RecursoSchemas.parseEstado(estado),
                facultadCriteria.id(),
                facultadCriteria.nombre(),
                page,
                size));
    }

    @GetMapping("/{id}")
    public RecursoDetalleResponse obtener(@PathVariable UUID id) {
        return mapper.toDetalle(gestionRecursos.obtener(id));
    }

    @GetMapping("/{id}/disponibilidad")
    public DisponibilidadResponse disponibilidad(@PathVariable UUID id) {
        return mapper.toDisponibilidad(gestionRecursos.disponibilidad(id));
    }

    @PatchMapping("/{id}/estado")
    public EstadoActualizadoResponse actualizarEstado(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarEstadoRequest request,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId) {

        return mapper.toEstadoActualizado(gestionRecursos.actualizarEstado(
                id,
                new ActualizarEstadoCommand(
                        request.nuevoEstado(),
                        request.motivo(),
                        request.usuarioResponsable()),
                correlationId));
    }
}
