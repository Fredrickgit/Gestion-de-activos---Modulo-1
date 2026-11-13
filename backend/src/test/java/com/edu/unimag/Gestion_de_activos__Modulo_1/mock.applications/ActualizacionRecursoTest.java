package com.edu.unimag.Gestion_de_activos__Modulo_1.mock.applications;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.ActualizacionRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EstadoDisponibilidadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActualizacionRecursoTest {

    @Mock
    private ActivoRepository activoRepository;

    @Mock
    private EspacioRepository espacioRepository;

    @Mock
    private EstadoDisponibilidadRepository estadoDisponibilidadRepository;

    @Mock
    private EstadoActivoRepository estadoActivoRepository;

    @InjectMocks
    private ActualizacionRecurso actualizacionRecurso;

    @Test
    @DisplayName("Actualizar estado de disponibilidad de activo exitosamente")
    void actualizarDisponibilidadActivoExitoso() {
        UUID activoId = UUID.randomUUID();
        UUID estadoViejoId = UUID.randomUUID();
        UUID estadoNuevoId = UUID.randomUUID();

        Activo activo = construirActivo(activoId, UUID.randomUUID(), estadoViejoId);

        when(estadoDisponibilidadRepository.existsById(estadoNuevoId)).thenReturn(true);
        when(activoRepository.findById(activoId)).thenReturn(Optional.of(activo));
        when(activoRepository.save(any(Activo.class))).thenAnswer(i -> i.getArgument(0));

        Activo actualizado = actualizacionRecurso.actualizarEstadoDisponibilidadActivo(activoId, estadoNuevoId);

        assertEquals(estadoNuevoId, actualizado.getDisponibilidadId());
        verify(activoRepository, times(1)).save(argThat(a ->
                a.getId().equals(activoId) && a.getDisponibilidadId().equals(estadoNuevoId)
        ));
    }

    @Test
    @DisplayName("Bloquear actualización si el recurso ya está en ese mismo estado")
    void actualizarDisponibilidadMismoEstado() {
        UUID activoId = UUID.randomUUID();
        UUID estadoActualId = UUID.randomUUID();

        Activo activo = construirActivo(activoId, UUID.randomUUID(), estadoActualId);

        when(estadoDisponibilidadRepository.existsById(estadoActualId)).thenReturn(true);
        when(activoRepository.findById(activoId)).thenReturn(Optional.of(activo));

        DomainException ex = assertThrows(DomainException.class,
                () -> actualizacionRecurso.actualizarEstadoDisponibilidadActivo(activoId, estadoActualId));

        assertEquals("El recurso ya se encuentra en el estado seleccionado.", ex.getMessage());
        verify(activoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar el estado físico de un activo usando enums")
    void actualizarEstadoFisicoActivoExitoso() {
        UUID activoId = UUID.randomUUID();
        UUID estadoFisicoViejoId = UUID.randomUUID();
        UUID estadoFisicoNuevoId = UUID.randomUUID();

        EstadoActivo estadoActivo = EstadoActivo.builder()
                .id(estadoFisicoNuevoId)
                .estado(EstadoFisicoActivo.OPTIMO)
                .build();
        Activo activo = construirActivo(activoId, estadoFisicoViejoId, UUID.randomUUID());

        when(estadoActivoRepository.findByEstado(EstadoFisicoActivo.OPTIMO)).thenReturn(Optional.of(estadoActivo));
        when(activoRepository.findById(activoId)).thenReturn(Optional.of(activo));
        when(activoRepository.save(any(Activo.class))).thenAnswer(i -> i.getArgument(0));

        Activo resultado = actualizacionRecurso.actualizarEstadoFisicoActivo(activoId, EstadoFisicoActivo.OPTIMO);

        assertEquals(estadoFisicoNuevoId, resultado.getEstadoFisicoId());
        verify(activoRepository, times(1)).save(argThat(a ->
                a.getId().equals(activoId) && a.getEstadoFisicoId().equals(estadoFisicoNuevoId)
        ));
    }

    private Activo construirActivo(UUID id, UUID estadoFisicoId, UUID disponibilidadId) {
        return Activo.builder()
                .id(id)
                .nombre("Proyector Epson")
                .codigo("ACT-001")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(estadoFisicoId)
                .disponibilidadId(disponibilidadId)
                .espacioId(UUID.randomUUID())
                .tipoActivoId(UUID.randomUUID())
                .build();
    }
}