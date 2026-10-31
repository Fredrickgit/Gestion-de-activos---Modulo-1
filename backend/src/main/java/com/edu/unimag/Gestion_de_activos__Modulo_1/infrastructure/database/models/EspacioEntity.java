package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EspacioEntity extends RecursoEntity {

    @Column(name = "aforo_maximo", nullable = false)
    private Integer aforoMaximo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "equipamiento", nullable = false, columnDefinition = "jsonb")
    private List<UUID> equipamiento = new ArrayList<>();

    @Column(name = "facultad_id", nullable = false)
    private UUID facultadId;

    @Column(name = "ubicacion_id", nullable = false)
    private UUID ubicacionId;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY)
    private List<ActivoEntity> activos = new ArrayList<>();
}
