package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class EspacioMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .aforo(model.getAforoMaximo() != null ? model.getAforoMaximo() : 0)
                .categoria(model.getCategoria())
                .plazoDevolucion(model.getPlazoDevolucion())
                .facultadId(model.getFacultadId())
                .disponibilidadId(model.getDisponibilidadId())
                .ubicacionId(model.getUbicacionId())
                .build();
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .aforoMaximo(domain.getAforo())
                .categoria(domain.getCategoria())
                .plazoDevolucion(domain.getPlazoDevolucion())
                .facultadId(domain.getFacultadId())
                .disponibilidadId(domain.getDisponibilidadId())
                .ubicacionId(domain.getUbicacionId())
                .equipamiento(new ArrayList<>())
                .build();
    }
}
