package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EstadoActualizadoView(
        UUID id,
        String tipo,
        String estadoAnterior,
        String estadoNuevo,
        String usuarioResponsable,
        OffsetDateTime timestamp
) {
}
