package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

@Component
public class UbicacionMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Ubicacion model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .descripcion(model.getDescripcion())
                .build();
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Ubicacion toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Ubicacion.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .build();
    }
}

