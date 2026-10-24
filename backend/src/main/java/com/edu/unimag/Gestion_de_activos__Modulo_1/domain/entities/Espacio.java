package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Espacio {

    private UUID id;
    private String nombre;
    private int aforo;
    private CategoriaRecurso categoria;
    private int plazoDevolucion;

    private UUID facultadId;
    private UUID disponibilidadId;
    private UUID ubicacionId;


}
