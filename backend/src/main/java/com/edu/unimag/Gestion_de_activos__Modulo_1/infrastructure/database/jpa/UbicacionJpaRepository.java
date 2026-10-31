package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.UbicacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data para la tabla {@code ubicacion}.
 */
@Repository
public interface UbicacionJpaRepository extends JpaRepository<UbicacionEntity, UUID> {

    Optional<UbicacionEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}

