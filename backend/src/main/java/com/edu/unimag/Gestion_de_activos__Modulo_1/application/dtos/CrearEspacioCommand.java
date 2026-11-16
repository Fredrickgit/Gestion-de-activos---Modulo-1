package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.List;
import java.util.UUID;

public record CrearEspacioCommand(
        String nombre,
        String codigo,
        Integer aforoMaximo,
        UUID facultadId,
        UUID ubicacionId,
        String edificio,
        List<UUID> equipamiento,
        Integer plazoDevolucion
) {
}
