package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.TipoActivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipoActivoJpaRepository extends JpaRepository<TipoActivoEntity, UUID> {

    Optional<TipoActivoEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}