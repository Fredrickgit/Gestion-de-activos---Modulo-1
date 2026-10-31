package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EspacioEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre la entidad JPA {@link EspacioEntity} y la entidad de dominio
 * {@link Espacio}. No contiene reglas de negocio: solo correspondencia de
 * campos.
 */
@Component
public class EspacioMapper {

    public Espacio toDomain(EspacioEntity entity) {
        if (entity == null) {
            return null;
        }

        return Espacio.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .aforo(entity.getAforoMaximo() != null ? entity.getAforoMaximo() : 0)
                .categoria(entity.getCategoria())
                .facultadId(entity.getFacultadId())
                .disponibilidadId(entity.getDisponibilidadId())
                .ubicacionId(entity.getUbicacionId())
                .build();
    }

    public EspacioEntity toEntity(Espacio domain) {
        if (domain == null) {
            return null;
        }

        EspacioEntity entity = new EspacioEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setAforoMaximo(domain.getAforo());
        entity.setCategoria(domain.getCategoria());
        entity.setFacultadId(domain.getFacultadId());
        entity.setDisponibilidadId(domain.getDisponibilidadId());
        entity.setUbicacionId(domain.getUbicacionId());
        return entity;
    }
}
