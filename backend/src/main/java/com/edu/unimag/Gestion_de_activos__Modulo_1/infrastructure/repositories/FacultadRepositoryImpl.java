package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.FacultadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.FacultadJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers.FacultadMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FacultadRepositoryImpl implements FacultadRepository {

    private final FacultadJpaRepository jpaRepository;
    private final FacultadMapper mapper;

    public FacultadRepositoryImpl(FacultadJpaRepository jpaRepository, FacultadMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Facultad save(Facultad entity) {
        var model = mapper.toModel(entity);
        var saved = jpaRepository.save(model);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Facultad> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Facultad> findAll() {
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
    public Optional<Facultad> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaRepository.existsByNombre(nombre);
    }
}
