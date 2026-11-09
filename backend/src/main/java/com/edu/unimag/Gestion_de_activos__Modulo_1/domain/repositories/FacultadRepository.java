package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;

import java.util.Optional;
import java.util.UUID;

public interface FacultadRepository extends BaseRepository<Facultad, UUID> {

    Optional<Facultad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}