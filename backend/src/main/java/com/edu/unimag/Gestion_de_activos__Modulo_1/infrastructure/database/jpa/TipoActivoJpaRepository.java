package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la tabla {@code tipo_activo}.
 */
@Repository
public interface TipoActivoJpaRepository extends JpaRepository<TipoActivo, UUID> {

    Optional<TipoActivo> findByNombre(String nombre);

    List<TipoActivo> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);
}
