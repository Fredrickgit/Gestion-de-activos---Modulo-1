package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.LoanPeriod;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Name;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TipoActivo {

    private final UUID id;
    private final Name nombre;
    private final String descripcion;
    private final LoanPeriod plazoDevolucion;

    @Builder
    public TipoActivo(UUID id, String nombre, String descripcion, int plazoDevolucion) {
        this.id = id;
        this.nombre = Name.of(nombre, "El nombre del tipo de activo es obligatorio");
        this.descripcion = descripcion;
        this.plazoDevolucion = LoanPeriod.of(plazoDevolucion);
    }

    public String getNombre() {
        return nombre.value();
    }

    public int getPlazoDevolucion() {
        return plazoDevolucion.value();
    }
}
