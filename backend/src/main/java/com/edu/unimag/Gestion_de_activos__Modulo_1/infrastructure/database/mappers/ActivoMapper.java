package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ActivoMapper {

    public com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo toDomain(
            com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Activo model) {
        if (model == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .codigo(model.getCodigo())
                .categoria(model.getCategoria())
                .estadoFisicoId(model.getEstadoFisicoId())
                .disponibilidadId(model.getDisponibilidadId())
                .espacioId(obtenerEspacioId(model))
                .tipoActivoId(model.getTipoActivoId())
                .plazoDevolucion(model.getPlazoDevolucion())
                .build();
    }

    public com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Activo toModel(
            com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo domain) {
        if (domain == null) {
            return null;
        }

        return com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Activo.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .codigo(domain.getCodigo())
                .categoria(domain.getCategoria())
                .plazoDevolucion(domain.getPlazoDevolucion())
                .estadoFisicoId(domain.getEstadoFisicoId())
                .disponibilidadId(domain.getDisponibilidadId())
                .tipoActivoId(domain.getTipoActivoId())
                .espacio(referenciaEspacio(domain.getEspacioId()))
                .build();
    }

    private UUID obtenerEspacioId(com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Activo model) {
        return model.getEspacio() != null ? model.getEspacio().getId() : null;
    }

    private Espacio referenciaEspacio(UUID espacioId) {
        if (espacioId == null) {
            return null;
        }
        Espacio espacio = new Espacio();
        espacio.setId(espacioId);
        return espacio;
    }
}
