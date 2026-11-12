package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio;
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
 * Repositorio Spring Data JPA para la tabla {@code espacio}.
 */
@Repository
public interface EspacioJpaRepository extends JpaRepository<Espacio, UUID> {

    Optional<Espacio> findByNombre(String nombre);

    List<Espacio> findByNombreContainingIgnoreCase(String nombre);

    Optional<Espacio> findByCodigo(String codigo);

    boolean existsByNombre(String nombre);

    boolean existsByCodigo(String codigo);

    List<Espacio> findByCategoria(CategoriaRecurso categoria);

    List<Espacio> findByFacultadId(UUID facultadId);

    List<Espacio> findByUbicacionId(UUID ubicacionId);

    List<Espacio> findByDisponibilidadId(UUID disponibilidadId);

    List<Espacio> findByAforoMaximoGreaterThanEqual(Integer aforoMinimo);

    boolean existsByFacultadId(UUID facultadId);

    boolean existsByUbicacionId(UUID ubicacionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Espacio e WHERE e.id = :id")
    Optional<Espacio> findByIdForUpdate(@Param("id") UUID id);
}
