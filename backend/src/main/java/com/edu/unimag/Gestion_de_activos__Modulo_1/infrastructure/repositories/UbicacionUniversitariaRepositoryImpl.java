package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.UbicacionUniversitariaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.UbicacionJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers.UbicacionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UbicacionUniversitariaRepositoryImpl implements UbicacionUniversitariaRepository {

    private final UbicacionJpaRepository jpaRepository;
    private final UbicacionMapper mapper;

    public UbicacionUniversitariaRepositoryImpl(UbicacionJpaRepository jpaRepository, UbicacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public UbicacionUniversitaria save(UbicacionUniversitaria entity) {
        var model = mapper.toModel(entity);
        var saved = jpaRepository.save(model);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UbicacionUniversitaria> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<UbicacionUniversitaria> findAll() {
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
    public Optional<UbicacionUniversitaria> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return jpaRepository.existsByNombre(nombre);
    }
}
