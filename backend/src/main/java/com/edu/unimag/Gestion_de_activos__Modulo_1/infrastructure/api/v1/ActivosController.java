package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearActivoCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CrearActivoRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.mappers.RecursoApiMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registro de activos físicos (POST /api/v1/activos).
 */
@RestController
@RequestMapping("/api/v1/activos")
public class ActivosController {

    private final GestionRecursos gestionRecursos;
    private final RecursoApiMapper mapper;

    public ActivosController(GestionRecursos gestionRecursos, RecursoApiMapper mapper) {
        this.gestionRecursos = gestionRecursos;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoDetalleResponse crear(@Valid @RequestBody CrearActivoRequest request) {
        return mapper.toDetalle(gestionRecursos.crearActivo(new CrearActivoCommand(
                request.nombre(),
                request.codigo(),
                request.estadoFisico(),
                request.ubicacionId(),
                request.facultadId(),
                request.tipoActivoId(),
                request.plazoDevolucion())));
    }
}
