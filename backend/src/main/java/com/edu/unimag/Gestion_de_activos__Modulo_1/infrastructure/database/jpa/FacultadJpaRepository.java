package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la tabla {@code facultad}.
 */
@Repository
public interface FacultadJpaRepository extends JpaRepository<Facultad, UUID> {

    Optional<Facultad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
