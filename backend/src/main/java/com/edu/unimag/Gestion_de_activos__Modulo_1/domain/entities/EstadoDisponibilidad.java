package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities;


import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import lombok.*;


import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoDisponibilidad {

    private UUID id;
    private EstadoDisponibilidadRecurso disponibilidadRecurso;
    private String descripcion; //posiblemente se elimine


}
