package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import java.util.UUID;

public record CrearActivoOutput(
        UUID id,
        String nombre,
        String codigo,
        CategoriaRecurso categoria,
        UUID estadoFisicoId,
        UUID disponibilidadId,
        UUID espacioId,
        UUID tipoActivoId
) {
}
