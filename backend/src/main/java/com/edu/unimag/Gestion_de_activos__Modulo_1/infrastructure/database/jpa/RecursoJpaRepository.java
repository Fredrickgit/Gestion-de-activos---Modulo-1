package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data para la entidad base {@link RecursoEntity}.
 */
@Repository
public interface RecursoJpaRepository extends JpaRepository<RecursoEntity, UUID> {

    List<RecursoEntity> findByCategoria(CategoriaRecurso categoria);

    List<RecursoEntity> findByDisponibilidadId(UUID disponibilidadId);
}

