package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_recurso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistorialRecursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_recurso", nullable = false)
    private UUID idRecurso;

    @Column(name = "estado_anterior", nullable = false, length = 50)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, length = 50)
    private String estadoNuevo;

    @Column(name = "usuario_responsable", nullable = false)
    private String usuarioResponsable;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    // private String motivo;

}
