package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.HistorialRecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data para la tabla {@code historial_recurso}.
 */
@Repository
public interface HistorialRecursoJpaRepository extends JpaRepository<HistorialRecursoEntity, UUID> {

    List<HistorialRecursoEntity> findByIdRecurso(UUID idRecurso);

    List<HistorialRecursoEntity> findByIdRecursoOrderByFechaEventoDesc(UUID idRecurso);
}

