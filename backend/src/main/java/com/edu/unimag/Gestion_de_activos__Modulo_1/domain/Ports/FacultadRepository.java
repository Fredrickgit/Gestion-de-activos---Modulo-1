package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FacultadRepository {

    Facultad save(Facultad entity);

    Optional<Facultad> findById(UUID id);

    List<Facultad> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<Facultad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
