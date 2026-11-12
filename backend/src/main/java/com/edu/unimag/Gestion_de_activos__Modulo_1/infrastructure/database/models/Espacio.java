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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "espacio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Espacio {

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
    private CategoriaRecurso categoria = CategoriaRecurso.ESPACIO;

    @Column(name = "aforo_maximo", nullable = false)
    private Integer aforoMaximo;

    @Column(name = "plazo_devolucion", nullable = false)
    private int plazoDevolucion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "equipamiento", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private List<UUID> equipamiento = new ArrayList<>();

    @Column(name = "facultad_id", nullable = false)
    private UUID facultadId;

    @Column(name = "ubicacion_id", nullable = false)
    private UUID ubicacionId;

    @Column(name = "disponibilidad_id", nullable = false)
    private UUID disponibilidadId;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Activo> activos = new ArrayList<>();
}

