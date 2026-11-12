package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

@Component
public class TipoActivoMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .descripcion(model.getDescripcion())
                .plazoDevolucion(model.getPlazoDevolucion())
                .build();
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .plazoDevolucion(domain.getPlazoDevolucion())
                .build();
    }
}
