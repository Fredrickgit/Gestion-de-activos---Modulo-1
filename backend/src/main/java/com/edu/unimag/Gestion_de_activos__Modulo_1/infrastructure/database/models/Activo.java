package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "activo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CategoriaRecurso categoria = CategoriaRecurso.ACTIVO;

    @Column(name = "plazo_devolucion", nullable = false)
    private int plazoDevolucion;

    @Column(name = "estado_fisico_id", nullable = false)
    private UUID estadoFisicoId;

    @Column(name = "disponibilidad_id", nullable = false)
    private UUID disponibilidadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;
}

