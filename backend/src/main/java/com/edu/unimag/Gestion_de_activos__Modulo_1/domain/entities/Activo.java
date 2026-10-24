package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activo {

    private UUID id;
    private String nombre;
    private String codigo;
    private CategoriaRecurso categoria;
    private int plazoDevolucion;

    private UUID estadoFisicoId;
    private UUID disponibilidadId;
    private UUID espacioId;


}
