package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EventoSaliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio del outbox de eventos salientes.
 */
@Repository
public interface EventoSalienteJpaRepository extends JpaRepository<EventoSaliente, UUID> {

    List<EventoSaliente> findTop50ByEnviadoEnIsNullOrderByCreadoEnAsc();
}
