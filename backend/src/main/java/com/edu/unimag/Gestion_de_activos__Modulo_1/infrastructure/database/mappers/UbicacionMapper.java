package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Coordinates;
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
                .coordenadas(parseCoordenadas(model.getCoordenadas()))
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
                .coordenadas(formatearCoordenadas(domain.getCoordenadas()))
                .build();
    }

    private Coordinates parseCoordenadas(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String[] partes = valor.split(",");
        if (partes.length != 2) {
            return null;
        }
        try {
            return Coordinates.of(
                    Double.parseDouble(partes[0].trim()),
                    Double.parseDouble(partes[1].trim()));
        } catch (NumberFormatException | com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException ex) {
            return null;
        }
    }

    private String formatearCoordenadas(Coordinates coordenadas) {
        return coordenadas == null ? null : coordenadas.toString();
    }
}
