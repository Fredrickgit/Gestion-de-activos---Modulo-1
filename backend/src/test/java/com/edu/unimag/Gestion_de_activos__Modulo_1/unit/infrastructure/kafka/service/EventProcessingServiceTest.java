package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.kafka.service;

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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
