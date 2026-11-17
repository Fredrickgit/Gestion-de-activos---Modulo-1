package com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.HistorialRecursoView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface HistorialRecursoPort {

    void registrar(UUID recursoId,
                   String estadoAnterior,
                   String estadoNuevo,
                   String usuarioResponsable,
                   LocalDateTime fechaEvento,
                   String motivo);

    /**
     * Lista los movimientos de un recurso, del más reciente al más antiguo.
     */
    default List<HistorialRecursoView> listar(UUID recursoId) {
        return List.of();
    }
}
