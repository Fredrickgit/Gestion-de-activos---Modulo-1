package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

@Component
public class EstadoDisponibilidadMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidad model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad.builder()
                .id(model.getId())
                .disponibilidadRecurso(model.getDisponibilidadRecurso())
                .descripcion(model.getDescripcion())
                .build();
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidad toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidad.builder()
                .id(domain.getId())
                .disponibilidadRecurso(domain.getDisponibilidadRecurso())
                .descripcion(domain.getDescripcion())
                .build();
    }
}
