package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.repositories;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoDisponibilidadJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.mappers.EstadoDisponibilidadMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EstadoDisponibilidadRepositoryImpl implements EstadoDisponibilidadRepository {

    private final EstadoDisponibilidadJpaRepository jpaRepository;
    private final EstadoDisponibilidadMapper mapper;

    public EstadoDisponibilidadRepositoryImpl(EstadoDisponibilidadJpaRepository jpaRepository, EstadoDisponibilidadMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public EstadoDisponibilidad save(EstadoDisponibilidad entity) {
        var model = mapper.toModel(entity);
        // Flush para que la lectura CQRS posterior vea la fila recién insertada.
        var saved = jpaRepository.saveAndFlush(model);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EstadoDisponibilidad> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<EstadoDisponibilidad> findAll() {
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
    public Optional<EstadoDisponibilidad> findByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso) {
        return jpaRepository.findByDisponibilidadRecurso(disponibilidadRecurso).map(mapper::toDomain);
    }

    @Override
    public boolean existsByDisponibilidadRecurso(EstadoDisponibilidadRecurso disponibilidadRecurso) {
        return jpaRepository.existsByDisponibilidadRecurso(disponibilidadRecurso);
    }
}
