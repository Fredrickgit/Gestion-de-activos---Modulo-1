package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.time.OffsetDateTime;

public record HorarioOcupacionResponse(
        OffsetDateTime inicio,
        OffsetDateTime fin
) {
}
