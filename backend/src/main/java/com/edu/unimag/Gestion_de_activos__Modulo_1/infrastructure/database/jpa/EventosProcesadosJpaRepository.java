package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EventosProcesadosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data para la tabla {@code eventos_procesados} (patrón Transactional Inbox).
 */
@Repository
public interface EventosProcesadosJpaRepository extends JpaRepository<EventosProcesadosEntity, UUID> {

    List<EventosProcesadosEntity> findByRecursoId(UUID recursoId);

    List<EventosProcesadosEntity> findByTipoEvento(String tipoEvento);
}

