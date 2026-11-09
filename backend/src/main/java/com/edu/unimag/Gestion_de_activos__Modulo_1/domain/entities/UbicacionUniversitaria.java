package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Coordinates;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Name;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UbicacionUniversitaria {

    private final UUID id;
    private final Name nombre;
    private final String descripcion;
    private final Coordinates coordenadas;

    public UbicacionUniversitaria(UUID id, String nombre, String descripcion) {
        this(id, nombre, descripcion, null);
    }

    @Builder
    public UbicacionUniversitaria(UUID id, String nombre, String descripcion, Coordinates coordenadas) {
        this.id = id;
        this.nombre = Name.of(nombre, "El nombre de la ubicacion es obligatorio");
        this.descripcion = descripcion;
        this.coordenadas = coordenadas;
    }

    public String getNombre() {
        return nombre.value();
    }
}
