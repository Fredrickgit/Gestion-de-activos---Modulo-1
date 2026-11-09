package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivoRepository extends BaseRepository<Activo, UUID> {

    Optional<Activo> findByCodigo(String codigo);

    List<Activo> findByCategoria(CategoriaRecurso categoria);

    List<Activo> findByEspacioId(UUID espacioId);

    List<Activo> findByDisponibilidadId(UUID disponibilidadId);

    List<Activo> findByEstadoFisicoId(UUID estadoFisicoId);

    boolean existsByEspacioId(UUID espacioId);

    boolean existsByCodigo(String codigo);
}