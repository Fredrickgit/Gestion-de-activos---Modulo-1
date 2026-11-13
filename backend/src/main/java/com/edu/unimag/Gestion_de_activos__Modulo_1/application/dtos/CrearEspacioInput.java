package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.UUID;

public record CrearEspacioInput(
        String nombre,
        String codigo,
        int aforo,
        UUID facultadId,
        UUID ubicacionId,
        Integer plazoDevolucion
) {

    public CrearEspacioInput {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del espacio es obligatorio");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del espacio es obligatorio");
        }
        if (aforo <= 0) {
            throw new IllegalArgumentException("El aforo debe ser mayor que cero");
        }
    }

    public boolean esValido() {
        return nombre != null && !nombre.isBlank()
                && codigo != null && !codigo.isBlank()
                && aforo > 0;
    }
}
