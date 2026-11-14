package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivoRepository {

    Activo save(Activo entity);

    Optional<Activo> findById(UUID id);

    Optional<Activo> findByIdForUpdate(UUID id);

    List<Activo> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);

    Optional<Activo> findByCodigo(String codigo);

    List<Activo> findByNombre(String nombre);

    List<Activo> findByNombreContainingIgnoreCase(String nombre);

    List<Activo> findByCategoria(CategoriaRecurso categoria);

    List<Activo> findByEspacioId(UUID espacioId);

    List<Activo> findByDisponibilidadId(UUID disponibilidadId);

    List<Activo> findByEstadoFisicoId(UUID estadoFisicoId);

    List<Activo> findByTipoActivoId(UUID tipoActivoId);

    boolean existsByEspacioId(UUID espacioId);

    boolean existsByTipoActivoId(UUID tipoActivoId);

    boolean existsByCodigo(String codigo);

    /**
     * Marca el activo como dado de baja lógicamente.
     *
     * @return número de filas afectadas (0 si el recurso no existe)
     */
    int marcarBaja(UUID id);
}
