package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EspacioRepository extends BaseRepository<Espacio, UUID> {

    Optional<Espacio> findByNombre(String nombre);

    List<Espacio> findByCategoria(CategoriaRecurso categoria);

    List<Espacio> findByFacultadId(UUID facultadId);

    List<Espacio> findByUbicacionId(UUID ubicacionId);

    List<Espacio> findByDisponibilidadId(UUID disponibilidadId);

    List<Espacio> findByAforoGreaterThanEqual(int aforoMinimo);

    boolean existsByFacultadId(UUID facultadId);

    boolean existsByUbicacionId(UUID ubicacionId);

    boolean existsByNombre(String nombre);
}

