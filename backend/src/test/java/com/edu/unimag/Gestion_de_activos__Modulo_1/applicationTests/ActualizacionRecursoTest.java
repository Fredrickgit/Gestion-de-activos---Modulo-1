package com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EspacioRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EstadoActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.repositories.EstadoDisponibilidadRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services.ActualizacionRecurso;
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

        Activo activo = Activo.builder().id(activoId).disponibilidadId(estadoViejoId).build();

        when(estadoDisponibilidadRepository.existsById(estadoNuevoId)).thenReturn(true);
        when(activoRepository.findById(activoId)).thenReturn(Optional.of(activo));
        when(activoRepository.save(any(Activo.class))).thenAnswer(i -> i.getArgument(0));

        Activo actualizado = actualizacionRecurso.actualizarEstadoDisponibilidadActivo(activoId, estadoNuevoId);

        assertEquals(estadoNuevoId, actualizado.getDisponibilidadId());
        verify(activoRepository, times(1)).save(activo);
    }

    @Test
    @DisplayName("Bloquear actualización si el recurso ya está en ese mismo estado")
    void actualizarDisponibilidadMismoEstado() {
        UUID activoId = UUID.randomUUID();
        UUID estadoActualId = UUID.randomUUID();

        Activo activo = Activo.builder().id(activoId).disponibilidadId(estadoActualId).build();

        when(estadoDisponibilidadRepository.existsById(estadoActualId)).thenReturn(true);
        when(activoRepository.findById(activoId)).thenReturn(Optional.of(activo));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
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

        EstadoActivo estadoActivo = EstadoActivo.builder().id(estadoFisicoNuevoId).estado(EstadoFisicoActivo.OPTIMO).build();
        Activo activo = Activo.builder().id(activoId).estadoFisicoId(estadoFisicoViejoId).build();

        when(estadoActivoRepository.findByEstado(EstadoFisicoActivo.OPTIMO)).thenReturn(Optional.of(estadoActivo));
        when(activoRepository.findById(activoId)).thenReturn(Optional.of(activo));
        when(activoRepository.save(any(Activo.class))).thenAnswer(i -> i.getArgument(0));

        Activo resultado = actualizacionRecurso.actualizarEstadoFisicoActivo(activoId, EstadoFisicoActivo.OPTIMO);

        assertEquals(estadoFisicoNuevoId, resultado.getEstadoFisicoId());
    }
}