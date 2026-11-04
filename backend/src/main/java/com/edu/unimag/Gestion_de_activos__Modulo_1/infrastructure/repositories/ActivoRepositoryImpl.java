package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.ActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers.ActivoMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ActivoRepositoryImpl implements ActivoRepository {

    private final ActivoJpaRepository jpaRepository;
    private final ActivoMapper mapper;

    public ActivoRepositoryImpl(ActivoJpaRepository jpaRepository, ActivoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Activo save(Activo entity) {
        var model = mapper.toModel(entity);
        var saved = jpaRepository.save(model);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Activo> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Activo> findAll() {
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
    public Optional<Activo> findByCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo).map(mapper::toDomain);
    }

    @Override
    public List<Activo> findByCategoria(CategoriaRecurso categoria) {
        return jpaRepository.findByCategoria(categoria).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Activo> findByEspacioId(UUID espacioId) {
        return jpaRepository.findByEspacioId(espacioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Activo> findByDisponibilidadId(UUID disponibilidadId) {
        return jpaRepository.findByDisponibilidadId(disponibilidadId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Activo> findByEstadoFisicoId(UUID estadoFisicoId) {
        return jpaRepository.findByEstadoFisicoId(estadoFisicoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEspacioId(UUID espacioId) {
        return jpaRepository.existsByEspacioId(espacioId);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return jpaRepository.existsByCodigo(codigo);
    }
}
