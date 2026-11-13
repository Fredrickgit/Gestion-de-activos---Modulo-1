package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.v1;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.CrearEspacioRequest;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos.RecursoDetalleResponse;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.service.RecursoApiService;
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

    private final RecursoApiService recursoApiService;

    public EspaciosController(RecursoApiService recursoApiService) {
        this.recursoApiService = recursoApiService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecursoDetalleResponse crear(@Valid @RequestBody CrearEspacioRequest request) {
        return recursoApiService.crearEspacio(request);
    }
}
