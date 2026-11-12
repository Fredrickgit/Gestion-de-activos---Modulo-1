package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstadoActivoRepository {

    EstadoActivo save(EstadoActivo entity);

    Optional<EstadoActivo> findById(UUID id);

    List<EstadoActivo> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<EstadoActivo> findByEstado(EstadoFisicoActivo estado);

    boolean existsByEstado(EstadoFisicoActivo estado);
}
