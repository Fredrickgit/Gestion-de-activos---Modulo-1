package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UbicacionUniversitariaRepository {

    UbicacionUniversitaria save(UbicacionUniversitaria entity);

    Optional<UbicacionUniversitaria> findById(UUID id);

    List<UbicacionUniversitaria> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<UbicacionUniversitaria> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
