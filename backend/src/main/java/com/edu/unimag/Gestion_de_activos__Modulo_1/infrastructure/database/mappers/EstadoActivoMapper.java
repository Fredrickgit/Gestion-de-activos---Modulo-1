package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

@Component
public class EstadoActivoMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoActivo model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo.builder()
                .id(model.getId())
                .estado(model.getEstado())
                .descripcion(model.getDescripcion())
                .build();
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoActivo toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoActivo.builder()
                .id(domain.getId())
                .estado(domain.getEstado())
                .descripcion(domain.getDescripcion())
                .build();
    }
}

