package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facultad {

    private UUID id;
    private String nombre;
    private String descripcion; //posiblemente se elimina


}
