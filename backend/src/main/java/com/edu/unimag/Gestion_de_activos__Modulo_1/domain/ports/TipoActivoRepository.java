package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TipoActivoRepository {

    TipoActivo save(TipoActivo entity);

    Optional<TipoActivo> findById(UUID id);

    List<TipoActivo> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<TipoActivo> findByNombre(String nombre);

    List<TipoActivo> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombre(String nombre);
}
