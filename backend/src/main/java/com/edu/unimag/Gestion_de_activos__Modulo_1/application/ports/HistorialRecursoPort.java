package com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports;

import java.time.LocalDateTime;
import java.util.UUID;

public interface HistorialRecursoPort {

    void registrar(UUID recursoId,
                   String estadoAnterior,
                   String estadoNuevo,
                   String usuarioResponsable,
                   LocalDateTime fechaEvento,
                   String motivo);
}
