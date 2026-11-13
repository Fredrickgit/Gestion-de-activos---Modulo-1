package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.AvailabilityRequiredException;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EstadoDisponibilidad {

    private final UUID id;
    private final EstadoDisponibilidadRecurso disponibilidadRecurso;
    private final String descripcion;

    @Builder
    public EstadoDisponibilidad(UUID id, EstadoDisponibilidadRecurso disponibilidadRecurso, String descripcion) {
        if (disponibilidadRecurso == null) {
            throw new AvailabilityRequiredException("La disponibilidad del recurso es obligatoria");
        }
        this.id = id;
        this.disponibilidadRecurso = disponibilidadRecurso;
        this.descripcion = descripcion;
    }
}
