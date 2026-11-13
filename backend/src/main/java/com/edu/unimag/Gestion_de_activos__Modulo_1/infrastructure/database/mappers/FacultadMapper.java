package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

@Component
public class FacultadMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Facultad model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .descripcion(model.getDescripcion())
                .build();
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Facultad toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Facultad.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .build();
    }
}

