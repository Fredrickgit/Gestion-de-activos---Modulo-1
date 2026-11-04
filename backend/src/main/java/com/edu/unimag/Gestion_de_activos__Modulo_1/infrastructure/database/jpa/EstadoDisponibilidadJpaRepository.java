package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para el catálogo {@code estado_disponibilidad}.
 */
@Repository
public interface EstadoDisponibilidadJpaRepository extends JpaRepository<EstadoDisponibilidad, UUID> {

    Optional<EstadoDisponibilidad> findByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso);

    boolean existsByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso);
}
