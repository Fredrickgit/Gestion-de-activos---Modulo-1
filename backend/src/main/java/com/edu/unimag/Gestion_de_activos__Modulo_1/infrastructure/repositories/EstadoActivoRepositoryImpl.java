package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers.EstadoActivoMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EstadoActivoRepositoryImpl implements EstadoActivoRepository {

    private final EstadoActivoJpaRepository jpaRepository;
    private final EstadoActivoMapper mapper;

    public EstadoActivoRepositoryImpl(EstadoActivoJpaRepository jpaRepository, EstadoActivoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public EstadoActivo save(EstadoActivo entity) {
        var model = mapper.toModel(entity);
        var saved = jpaRepository.save(model);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EstadoActivo> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<EstadoActivo> findAll() {
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
    public Optional<EstadoActivo> findByEstado(EstadoFisicoActivo estado) {
        return jpaRepository.findByEstado(estado).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEstado(EstadoFisicoActivo estado) {
        return jpaRepository.existsByEstado(estado);
    }
}
