package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para el catálogo {@code estado_activo}.
 */
@Repository
public interface EstadoActivoJpaRepository extends JpaRepository<EstadoActivo, UUID> {

    Optional<EstadoActivo> findByEstado(EstadoFisicoActivo estado);

    boolean existsByEstado(EstadoFisicoActivo estado);
}
