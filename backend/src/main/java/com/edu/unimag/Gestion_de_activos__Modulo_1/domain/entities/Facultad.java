package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Name;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Facultad {

    private final UUID id;
    private final Name nombre;
    private final String descripcion;

    @Builder
    public Facultad(UUID id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = Name.of(nombre, "El nombre de la facultad es obligatorio");
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre.value();
    }
}
