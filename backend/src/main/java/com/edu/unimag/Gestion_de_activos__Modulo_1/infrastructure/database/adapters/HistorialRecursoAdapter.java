package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.adapters;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos.HistorialRecursoView;
import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.HistorialRecursoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.HistorialRecursoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.HistorialRecurso;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public List<HistorialRecursoView> listar(UUID recursoId) {
        return historialRepository.findByIdRecursoOrderByFechaEventoDesc(recursoId)
                .stream()
                .map(entry -> new HistorialRecursoView(
                        entry.getId(),
                        entry.getIdRecurso(),
                        entry.getEstadoAnterior(),
                        entry.getEstadoNuevo(),
                        entry.getUsuarioResponsable(),
                        entry.getFechaEvento(),
                        entry.getMotivo()))
                .toList();
    }
}
