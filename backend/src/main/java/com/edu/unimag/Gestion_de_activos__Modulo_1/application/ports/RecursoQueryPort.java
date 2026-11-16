package com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoDetalleView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoFiltro;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.RecursoResumenView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para el modelo de lectura (CQRS ligero) del catálogo.
 */
public interface RecursoQueryPort {

    List<RecursoResumenView> listar(RecursoFiltro filtro,
                                    String orden,
                                    String direccion,
                                    int page,
                                    int size);

    long contar(RecursoFiltro filtro);

    Optional<RecursoDetalleView> findDetalle(UUID id);
}
