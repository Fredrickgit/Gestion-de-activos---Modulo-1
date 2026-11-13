package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import java.util.UUID;

public record CrearActivoInput(
        String nombre,
        String codigo,
        UUID espacioId,
        UUID estadoFisicoId,
        UUID tipoActivoId
) {

    public CrearActivoInput {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del activo es obligatorio");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del activo es obligatorio");
        }
    }

    public boolean esValido() {
        return nombre != null && !nombre.isBlank()
                && codigo != null && !codigo.isBlank();
    }
}
