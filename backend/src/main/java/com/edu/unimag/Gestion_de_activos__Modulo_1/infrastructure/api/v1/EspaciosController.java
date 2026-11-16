package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.CrearEspacioCommand;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GestionRecursos;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CrearEspacioRequest;
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
 * Registro de espacios físicos (POST /api/v1/espacios).
 */
@RestController
@RequestMapping("/api/v1/espacios")
public class EspaciosController {

    private final GestionRecursos gestionRecursos;
    private final RecursoApiMapper mapper;

    public EspaciosController(GestionRecursos gestionRecursos, RecursoApiMapper mapper) {
        this.gestionRecursos = gestionRecursos;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoDetalleResponse crear(@Valid @RequestBody CrearEspacioRequest request) {
        return mapper.toDetalle(gestionRecursos.crearEspacio(new CrearEspacioCommand(
                request.nombre(),
                request.codigo(),
                request.aforoMaximo(),
                request.facultadId(),
                request.ubicacionId(),
                request.edificio(),
                request.equipamiento(),
                request.plazoDevolucion())));
    }
}
