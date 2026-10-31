package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Recurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface RecursoRepository extends BaseRepository<Recurso, UUID> {

    Optional<Recurso> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);


    List<Recurso> findByNombre(String nombre);



    List<Recurso> findByCategoria(CategoriaRecurso categoria);

    List<Recurso> findByDisponibilidadId(UUID disponibilidadId);

    //para ACTIVO

    List<Recurso> findByEstadoFisicoId(UUID estadoFisicoId);

    List<Recurso> findByEspacioId(UUID espacioId);

    boolean existsByEspacioId(UUID espacioId);

    //para ESPACIO

    List<Recurso> findByFacultadId(UUID facultadId);

    List<Recurso> findByUbicacionId(UUID ubicacionId);

    List<Recurso> findByAforoGreaterThanEqual(int aforoMinimo);

    boolean existsByFacultadId(UUID facultadId);

    boolean existsByUbicacionId(UUID ubicacionId);

    Pagina<Recurso> buscar(CategoriaRecurso categoria,
                           EstadoDisponibilidadRecurso estado,
                           UUID facultadId,
                           int page,
                           int size);
}