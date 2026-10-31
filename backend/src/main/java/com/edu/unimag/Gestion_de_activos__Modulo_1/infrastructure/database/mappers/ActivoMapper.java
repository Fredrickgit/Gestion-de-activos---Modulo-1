package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.ActivoEntity;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EspacioEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ActivoMapper {

    public Activo toDomain(ActivoEntity entity) {
        if (entity == null) {
            return null;
        }

        return Activo.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .categoria(entity.getCategoria())
                .estadoFisicoId(entity.getEstadoFisicoId())
                .disponibilidadId(entity.getDisponibilidadId())
                .espacioId(obtenerEspacioId(entity))
                .build();
    }

    public ActivoEntity toEntity(Activo domain) {
        if (domain == null) {
            return null;
        }

        ActivoEntity entity = new ActivoEntity();
        entity.setId(domain.getId());
        entity.setNombre(domain.getNombre());
        entity.setCategoria(domain.getCategoria());
        entity.setEstadoFisicoId(domain.getEstadoFisicoId());
        entity.setDisponibilidadId(domain.getDisponibilidadId());
        entity.setEspacio(referenciaEspacio(domain.getEspacioId()));
        return entity;
    }

    private UUID obtenerEspacioId(ActivoEntity entity) {
        return entity.getEspacio() != null ? entity.getEspacio().getId() : null;
    }

    /**
     * Construye una referencia liviana por id. No se persiste por cascada:
     * solo se usa para resolver la clave foranea del activo.
     */
    private EspacioEntity referenciaEspacio(UUID espacioId) {
        if (espacioId == null) {
            return null;
        }
        EspacioEntity espacio = new EspacioEntity();
        espacio.setId(espacioId);
        return espacio;
    }
}
