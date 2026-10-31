package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EspacioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data para la tabla {@code espacio}.
 */
@Repository
public interface EspacioJpaRepository extends JpaRepository<EspacioEntity, UUID> {

    List<EspacioEntity> findByFacultadId(UUID facultadId);

    List<EspacioEntity> findByUbicacionId(UUID ubicacionId);
}
