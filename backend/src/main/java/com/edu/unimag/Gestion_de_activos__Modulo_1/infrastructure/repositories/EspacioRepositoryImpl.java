package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EspacioJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers.EspacioMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EspacioRepositoryImpl implements EspacioRepository {

    private final EspacioJpaRepository jpaRepository;
    private final EspacioMapper mapper;

    public EspacioRepositoryImpl(EspacioJpaRepository jpaRepository, EspacioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Espacio save(Espacio entity) {
        var model = mapper.toModel(entity);
        // saveAndFlush: la lectura CQRS posterior (JDBC, misma transacción)
        // debe ver la fila recién insertada. Con save() el INSERT podía
        // ejecutarse recién en el commit y la creación devolvía 404.
        var saved = jpaRepository.saveAndFlush(model);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Espacio> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Espacio> findByIdForUpdate(UUID id) {
        return jpaRepository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    public List<Espacio> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Optional<Espacio> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public List<Espacio> findByNombreContainingIgnoreCase(String nombre) {
        return jpaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Espacio> findByCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }

    @Override
    public List<Espacio> findByCategoria(CategoriaRecurso categoria) {
        return jpaRepository.findByCategoria(categoria).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Espacio> findByFacultadId(UUID facultadId) {
        return jpaRepository.findByFacultadId(facultadId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Espacio> findByUbicacionId(UUID ubicacionId) {
        return jpaRepository.findByUbicacionId(ubicacionId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Espacio> findByDisponibilidadId(UUID disponibilidadId) {
        return jpaRepository.findByDisponibilidadId(disponibilidadId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Espacio> findByAforoGreaterThanEqual(int aforoMinimo) {
        return jpaRepository.findByAforoMaximoGreaterThanEqual(aforoMinimo).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByFacultadId(UUID facultadId) {
        return jpaRepository.existsByFacultadId(facultadId);
    }

    @Override
    public boolean existsByUbicacionId(UUID ubicacionId) {
        return jpaRepository.existsByUbicacionId(ubicacionId);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaRepository.existsByNombre(nombre);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return jpaRepository.existsByCodigo(codigo);
    }
}
