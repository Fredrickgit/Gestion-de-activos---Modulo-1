package com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports;

import java.util.UUID;

public interface EstadoActualizadoPort {

    void publicar(String correlationId,
                  UUID recursoId,
                  String categoriaRecurso,
                  String estadoAnterior,
                  String nuevoEstado,
                  String motivo,
                  String usuarioResponsable);
}
