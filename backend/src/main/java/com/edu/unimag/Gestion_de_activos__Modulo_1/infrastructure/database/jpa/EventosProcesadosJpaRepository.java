package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EventosProcesados;
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
 * Repositorio Spring Data JPA para la tabla {@code eventos_procesados} (patrón Transactional Inbox).
 */
@Repository
public interface EventosProcesadosJpaRepository extends JpaRepository<EventosProcesados, UUID> {

    List<EventosProcesados> findByRecursoId(UUID recursoId);

    List<EventosProcesados> findByTipoEvento(String tipoEvento);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ep FROM EventosProcesados ep WHERE ep.eventoId = :eventoId")
    Optional<EventosProcesados> findByIdForUpdate(@Param("eventoId") UUID eventoId);
}
