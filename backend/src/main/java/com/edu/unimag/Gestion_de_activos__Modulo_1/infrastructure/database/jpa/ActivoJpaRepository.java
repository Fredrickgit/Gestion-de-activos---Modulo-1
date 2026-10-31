package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.ActivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data para la tabla {@code activo}.
 */
@Repository
public interface ActivoJpaRepository extends JpaRepository<ActivoEntity, UUID> {

    Optional<ActivoEntity> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<ActivoEntity> findByEspacioId(UUID espacioId);
}
