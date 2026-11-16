package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.time.OffsetDateTime;

public record HorarioOcupacionView(
        OffsetDateTime inicio,
        OffsetDateTime fin
) {
}
