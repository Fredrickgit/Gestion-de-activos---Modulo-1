package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recurso {

    // Atributos Generales
    private UUID id;
    private String nombre;
    private String codigo;
    private CategoriaRecurso categoria;
    private int plazoDevolucion;
    private UUID disponibilidadId;

    // para el ACTIVO
    private UUID estadoFisicoId;
    private UUID espacioId;

    // para el ESPACIO
    private Integer aforo;
    private UUID facultadId;
    private UUID ubicacionId;

    public boolean esActivo() {
        return categoria == CategoriaRecurso.ACTIVO;
    }

    public boolean esEspacio() {
        return categoria == CategoriaRecurso.ESPACIO;
    }
}