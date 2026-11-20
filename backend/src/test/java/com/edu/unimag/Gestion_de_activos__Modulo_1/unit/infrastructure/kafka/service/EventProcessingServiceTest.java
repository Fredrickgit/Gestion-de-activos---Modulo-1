package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.kafka.service;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.ActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EspacioJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoActivoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EstadoDisponibilidadJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EventosProcesadosJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.HistorialRecursoJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.DamageReportData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.EndedReservationData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.ResourceEventPayload;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.UseStartedData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.service.EventProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventProcessingServiceTest {

    @Mock
    private EventosProcesadosJpaRepository eventosProcesadosRepo;

    @Mock
    private ActivoJpaRepository activoRepo;

    @Mock
    private EspacioJpaRepository espacioRepo;

    @Mock
    private EstadoDisponibilidadJpaRepository estadoDisponibilidadRepo;

    @Mock
    private EstadoActivoJpaRepository estadoActivoRepo;

    @Mock
    private HistorialRecursoJpaRepository historialRecursoRepo;

    private EventProcessingService service;

    @BeforeEach
    void setUp() {
        service = new EventProcessingService(
                eventosProcesadosRepo, activoRepo, espacioRepo, estadoDisponibilidadRepo, estadoActivoRepo, historialRecursoRepo);
    }

    @Test
    void liberaEspacioVencidoYRegistraHistorial() {
        UUID enUsoId = UUID.randomUUID();
        UUID disponibleId = UUID.randomUUID();
        Espacio espacio = Espacio.builder()
                .id(UUID.randomUUID())
                .disponibilidadId(enUsoId)
                .usoTerminaEn(Instant.EPOCH)
                .build();
        EstadoDisponibilidad enUso = EstadoDisponibilidad.builder()
                .id(enUsoId).disponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO).build();
        EstadoDisponibilidad disponible = EstadoDisponibilidad.builder().id(disponibleId).build();
        when(espacioRepo.bloquearUsosVencidos(any(Instant.class))).thenReturn(List.of(espacio));
        when(estadoDisponibilidadRepo.findById(enUsoId)).thenReturn(Optional.of(enUso));
        when(estadoDisponibilidadRepo.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE))
                .thenReturn(Optional.of(disponible));

        service.liberarEspaciosVencidos();

        assertNull(espacio.getUsoTerminaEn());
        assertEquals(disponibleId, espacio.getDisponibilidadId());
        verify(historialRecursoRepo).save(any());
    }

    @Test
    @DisplayName("Un aviso de uso para un activo inexistente se rechaza para derivarlo a la DLT")
    void procesarNotificacionUsoConActivoInexistente() {
        UUID eventoId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        UUID enUsoId = UUID.randomUUID();

        UseStartedData data = new UseStartedData();
        data.setResourceId(recursoId);
        data.setResourceCategory("ACTIVO");
        data.setNewState("EN_USO");

        when(estadoDisponibilidadRepo.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO))
                .thenReturn(Optional.of(EstadoDisponibilidad.builder()
                        .id(enUsoId).disponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO).build()));
        when(activoRepo.findByIdForUpdate(recursoId)).thenReturn(Optional.empty());

        ResourceEventPayload<UseStartedData> event = new ResourceEventPayload<>(
                eventoId.toString(), "ResourceUseStarted", "1", OffsetDateTime.now(), "MODULE_2", data);

        assertThrows(IllegalArgumentException.class, () -> service.procesarNotificacionUso(event, "{}"));
    }

    @Test
    @DisplayName("Un aviso de uso para un espacio inexistente se rechaza para derivarlo a la DLT")
    void procesarNotificacionUsoConEspacioInexistente() {
        UUID eventoId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        UUID enUsoId = UUID.randomUUID();

        UseStartedData data = new UseStartedData();
        data.setResourceId(recursoId);
        data.setResourceCategory("ESPACIO");
        data.setNewState("EN_USO");
        data.setUseEndsAt(Instant.now().plus(1, ChronoUnit.HOURS));

        when(estadoDisponibilidadRepo.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO))
                .thenReturn(Optional.of(EstadoDisponibilidad.builder()
                        .id(enUsoId).disponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO).build()));
        when(espacioRepo.findByIdForUpdate(recursoId)).thenReturn(Optional.empty());

        ResourceEventPayload<UseStartedData> event = new ResourceEventPayload<>(
                eventoId.toString(), "ResourceUseStarted", "1", OffsetDateTime.now(), "MODULE_2", data);

        assertThrows(IllegalArgumentException.class, () -> service.procesarNotificacionUso(event, "{}"));
    }

    @Test
    @DisplayName("Un aviso solapado no acorta la hora de fin de uso ya programada")
    void noAcortaUsoTerminaEnConUnAvisoSolapado() {
        UUID eventoId = UUID.randomUUID();
        UUID recursoId = UUID.randomUUID();
        UUID disponibleId = UUID.randomUUID();
        UUID enUsoId = UUID.randomUUID();
        Instant finExistente = Instant.now().plus(2, ChronoUnit.HOURS);
        Instant finNuevo = Instant.now().plus(1, ChronoUnit.HOURS);

        Espacio espacio = Espacio.builder()
                .id(recursoId)
                .disponibilidadId(disponibleId)
                .usoTerminaEn(finExistente)
                .build();
        EstadoDisponibilidad disponible = EstadoDisponibilidad.builder()
                .id(disponibleId).disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE).build();

        UseStartedData data = new UseStartedData();
        data.setResourceId(recursoId);
        data.setResourceCategory("ESPACIO");
        data.setNewState("EN_USO");
        data.setUseEndsAt(finNuevo);

        when(estadoDisponibilidadRepo.findByDisponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO))
                .thenReturn(Optional.of(EstadoDisponibilidad.builder()
                        .id(enUsoId).disponibilidadRecurso(EstadoDisponibilidadRecurso.EN_USO).build()));
        when(espacioRepo.findByIdForUpdate(recursoId)).thenReturn(Optional.of(espacio));
        when(eventosProcesadosRepo.findByIdForUpdate(eventoId)).thenReturn(Optional.empty());
        when(estadoDisponibilidadRepo.findById(disponibleId)).thenReturn(Optional.of(disponible));

        ResourceEventPayload<UseStartedData> event = new ResourceEventPayload<>(
                eventoId.toString(), "ResourceUseStarted", "1", OffsetDateTime.now(), "MODULE_2", data);

        service.procesarNotificacionUso(event, "{}");

        assertEquals(finExistente, espacio.getUsoTerminaEn());
    }

    @Test
    @DisplayName("El evento de notificacion uso sin identificador no es procesable")
    void procesarNotificacionUsoSinEventoId() {
        ResourceEventPayload<UseStartedData> event = new ResourceEventPayload<>(
                null, "notificacion.uso", "1", null, "M2", new UseStartedData());

        assertThrows(IllegalArgumentException.class, () -> service.procesarNotificacionUso(event, "{}"));
    }

    @Test
    @DisplayName("El evento de notificacion uso sin datos del recurso no es procesable")
    void procesarNotificacionUsoSinDatos() {
        ResourceEventPayload<UseStartedData> event = new ResourceEventPayload<>(
                UUID.randomUUID().toString(), "notificacion.uso", "1", null, "M2", null);

        assertThrows(IllegalArgumentException.class, () -> service.procesarNotificacionUso(event, "{}"));
    }

    @Test
    @DisplayName("El evento de reserva finalizada sin datos del recurso no es procesable")
    void procesarReservaFinalizadaSinDatos() {
        ResourceEventPayload<EndedReservationData> event = new ResourceEventPayload<>(
                UUID.randomUUID().toString(), "reserva.finalizada", "1", null, "M3", null);

        assertThrows(IllegalArgumentException.class, () -> service.procesarReservaFinalizada(event, "{}"));
    }

    @Test
    @DisplayName("El evento de reporte de danos con identificador invalido no es procesable")
    void procesarReporteDanosConEventoIdInvalido() {
        ResourceEventPayload<DamageReportData> event = new ResourceEventPayload<>(
                "abc", "reporte.danos", "1", null, "M3", new DamageReportData());

        assertThrows(IllegalArgumentException.class, () -> service.procesarReporteDanos(event, "{}"));
    }
}
