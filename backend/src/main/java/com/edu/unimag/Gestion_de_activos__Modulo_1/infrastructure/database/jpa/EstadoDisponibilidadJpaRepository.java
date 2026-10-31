package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data para el catalogo {@code estado_disponibilidad}.
 */
@Repository
public interface EstadoDisponibilidadJpaRepository extends JpaRepository<EstadoDisponibilidadEntity, UUID> {

    Optional<EstadoDisponibilidadEntity> findByDisponibilidadRecurso(EstadoDisponibilidadRecurso estado);

    boolean existsByDisponibilidadRecurso(EstadoDisponibilidadRecurso estado);
}
