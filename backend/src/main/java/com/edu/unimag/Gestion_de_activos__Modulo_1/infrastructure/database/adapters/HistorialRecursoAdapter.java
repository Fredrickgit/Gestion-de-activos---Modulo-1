package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.adapters;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.HistorialRecursoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.HistorialRecursoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.HistorialRecurso;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adaptador de persistencia del historial de cambios de estado.
 */
@Component
public class HistorialRecursoAdapter implements HistorialRecursoPort {

    private final HistorialRecursoJpaRepository historialRepository;

    public HistorialRecursoAdapter(HistorialRecursoJpaRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Override
    public void registrar(UUID recursoId,
                          String estadoAnterior,
                          String estadoNuevo,
                          String usuarioResponsable,
                          LocalDateTime fechaEvento,
                          String motivo) {
        historialRepository.save(HistorialRecurso.builder()
                .idRecurso(recursoId)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .usuarioResponsable(usuarioResponsable)
                .fechaEvento(fechaEvento)
                .motivo(motivo)
                .build());
    }
}
