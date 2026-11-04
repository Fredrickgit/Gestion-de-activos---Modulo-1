package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;

import java.util.Optional;
import java.util.UUID;

public interface EstadoActivoRepository extends BaseRepository<EstadoActivo, UUID> {

    Optional<EstadoActivo> findByEstado(EstadoFisicoActivo estado);

    boolean existsByEstado(EstadoFisicoActivo estado);
}

