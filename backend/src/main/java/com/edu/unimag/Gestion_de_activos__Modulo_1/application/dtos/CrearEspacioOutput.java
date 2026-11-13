package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import java.util.UUID;

public record CrearEspacioOutput(
        UUID id,
        String nombre,
        String codigo,
        int aforo,
        CategoriaRecurso categoria,
        Integer plazoDevolucion,
        UUID facultadId,
        UUID disponibilidadId,
        UUID ubicacionId
) {
}
