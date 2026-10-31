package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;

import java.util.Optional;
import java.util.UUID;

public interface EstadoDisponibilidadRepository extends BaseRepository<EstadoDisponibilidad, UUID> {

    Optional<EstadoDisponibilidad> findByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso);

    boolean existsByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso);
}