package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Activo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la tabla {@code activo}.
 */
@Repository
public interface ActivoJpaRepository extends JpaRepository<Activo, UUID> {

    Optional<Activo> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Activo> findByCategoria(CategoriaRecurso categoria);

    List<Activo> findByEspacioId(UUID espacioId);

    List<Activo> findByDisponibilidadId(UUID disponibilidadId);

    List<Activo> findByEstadoFisicoId(UUID estadoFisicoId);

    boolean existsByEspacioId(UUID espacioId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Activo a WHERE a.id = :id")
    Optional<Activo> findByIdForUpdate(@Param("id") UUID id);
}
