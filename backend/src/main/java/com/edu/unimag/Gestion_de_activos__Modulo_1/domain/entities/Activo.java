package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCategoryException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.RequiredReferenceException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Code;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Name;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Activo {

    private final UUID id;
    private final Name nombre;
    private final Code codigo;
    private final CategoriaRecurso categoria;

    private final UUID estadoFisicoId;
    private final UUID disponibilidadId;
    private final UUID espacioId;
    private final UUID tipoActivoId;

    @Builder
    public Activo(UUID id, String nombre, String codigo, CategoriaRecurso categoria,
                  UUID estadoFisicoId, UUID disponibilidadId, UUID espacioId,
                  UUID tipoActivoId) {
        this.id = id;
        this.nombre = Name.of(nombre, "El nombre del activo es obligatorio");
        this.codigo = Code.of(codigo, "El codigo del activo es obligatorio");
        if (categoria != CategoriaRecurso.ACTIVO) {
            throw new InvalidCategoryException("La categoria del activo debe ser ACTIVO");
        }
        if (estadoFisicoId == null) {
            throw new RequiredReferenceException("El estado fisico del activo es obligatorio");
        }
        if (disponibilidadId == null) {
            throw new RequiredReferenceException("La disponibilidad del activo es obligatoria");
        }
        if (tipoActivoId == null) {
            throw new RequiredReferenceException("El tipo de activo es obligatorio");
        }
        this.categoria = categoria;
        this.estadoFisicoId = estadoFisicoId;
        this.disponibilidadId = disponibilidadId;
        this.espacioId = espacioId;
        this.tipoActivoId = tipoActivoId;
    }

    public String getNombre() {
        return nombre.value();
    }

    public String getCodigo() {
        return codigo.value();
    }
}
