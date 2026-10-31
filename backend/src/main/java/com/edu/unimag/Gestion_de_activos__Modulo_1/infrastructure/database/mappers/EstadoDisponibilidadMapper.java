package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidadEntity;
import org.springframework.stereotype.Component;

/**
 * Traduce entre la entidad JPA {@link EstadoDisponibilidadEntity} y la entidad
 * de dominio {@link EstadoDisponibilidad}.
 */
@Component
public class EstadoDisponibilidadMapper {

    public EstadoDisponibilidad toDomain(EstadoDisponibilidadEntity entity) {
        if (entity == null) {
            return null;
        }

        return EstadoDisponibilidad.builder()
                .id(entity.getId())
                .disponibilidadRecurso(entity.getDisponibilidadRecurso())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public EstadoDisponibilidadEntity toEntity(EstadoDisponibilidad domain) {
        if (domain == null) {
            return null;
        }

        EstadoDisponibilidadEntity entity = new EstadoDisponibilidadEntity();
        entity.setId(domain.getId());
        entity.setDisponibilidadRecurso(domain.getDisponibilidadRecurso());
        entity.setDescripcion(domain.getDescripcion());
        return entity;
    }
}
