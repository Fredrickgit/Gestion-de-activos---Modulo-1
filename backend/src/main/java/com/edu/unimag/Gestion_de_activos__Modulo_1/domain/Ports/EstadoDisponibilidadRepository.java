package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstadoDisponibilidadRepository {

    EstadoDisponibilidad save(EstadoDisponibilidad entity);

    Optional<EstadoDisponibilidad> findById(UUID id);

    List<EstadoDisponibilidad> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<EstadoDisponibilidad> findByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso);

    boolean existsByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso);
}
