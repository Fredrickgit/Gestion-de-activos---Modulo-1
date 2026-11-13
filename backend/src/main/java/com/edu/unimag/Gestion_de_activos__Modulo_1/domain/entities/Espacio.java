package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCategoryException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.RequiredReferenceException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Capacity;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Code;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.LoanPeriod;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Name;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Espacio {

    private final UUID id;
    private final Name nombre;
    private final Code codigo;
    private final Capacity aforo;
    private final CategoriaRecurso categoria;
    private final LoanPeriod plazoDevolucion;
    private final List<UUID> equipamiento;

    private final UUID facultadId;
    private final UUID disponibilidadId;
    private final UUID ubicacionId;

    @Builder
    public Espacio(UUID id, String nombre, String codigo, int aforo, CategoriaRecurso categoria, int plazoDevolucion,
                   UUID facultadId, UUID disponibilidadId, UUID ubicacionId, List<UUID> equipamiento) {
        this.id = id;
        this.nombre = Name.of(nombre, "El nombre del espacio es obligatorio");
        this.codigo = Code.of(codigo, "El codigo del espacio es obligatorio");
        this.aforo = Capacity.of(aforo);
        if (categoria != CategoriaRecurso.ESPACIO) {
            throw new InvalidCategoryException("La categoria del espacio debe ser ESPACIO");
        }
        this.plazoDevolucion = LoanPeriod.of(plazoDevolucion);
        if (disponibilidadId == null) {
            throw new RequiredReferenceException("La disponibilidad del espacio es obligatoria");
        }
        if (ubicacionId == null) {
            throw new RequiredReferenceException("La ubicacion del espacio es obligatoria");
        }
        this.categoria = categoria;
        this.facultadId = facultadId;
        this.disponibilidadId = disponibilidadId;
        this.ubicacionId = ubicacionId;
        this.equipamiento = equipamiento == null ? List.of() : List.copyOf(equipamiento);
    }

    public Espacio(UUID id, String nombre, String codigo, int aforo, CategoriaRecurso categoria, int plazoDevolucion,
                   UUID facultadId, UUID disponibilidadId, UUID ubicacionId) {
        this(id, nombre, codigo, aforo, categoria, plazoDevolucion, facultadId, disponibilidadId, ubicacionId, List.of());
    }

    public Espacio conDisponibilidad(UUID nuevaDisponibilidadId) {
        return new Espacio(id, getNombre(), getCodigo(), getAforo(), categoria,
                getPlazoDevolucion(), facultadId, nuevaDisponibilidadId, ubicacionId, equipamiento);
    }

    public String getNombre() {
        return nombre.value();
    }

    public String getCodigo() {
        return codigo.value();
    }

    public int getAforo() {
        return aforo.value();
    }

    public int getPlazoDevolucion() {
        return plazoDevolucion.value();
    }
}
