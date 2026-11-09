package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.PhysicalStateRequiredException;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EstadoActivo {

    private final UUID id;
    private final EstadoFisicoActivo estado;
    private final String descripcion;

    @Builder
    public EstadoActivo(UUID id, EstadoFisicoActivo estado, String descripcion) {
        if (estado == null) {
            throw new PhysicalStateRequiredException("El estado fisico del activo es obligatorio");
        }
        this.id = id;
        this.estado = estado;
        this.descripcion = descripcion;
    }
}
