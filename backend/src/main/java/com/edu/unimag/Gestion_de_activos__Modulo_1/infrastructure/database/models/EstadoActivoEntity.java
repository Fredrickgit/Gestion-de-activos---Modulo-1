package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Catalogo de estados fisicos de un activo.
 */
@Entity
@Table(name = "estado_activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoActivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private EstadoFisicoActivo estado;

    private String descripcion;
}
