package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.kafka.outbox;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EventoSalienteJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EventoSaliente;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.outbox.OutboxRelay;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.producer.EstadoActualizadoProducer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxRelayTest {

    @Mock
    private EventoSalienteJpaRepository eventoSalienteRepository;

    @Mock
    private EstadoActualizadoProducer estadoActualizadoProducer;

    private OutboxRelay relay;

    @BeforeEach
    void setUp() {
        relay = new OutboxRelay(eventoSalienteRepository, estadoActualizadoProducer, new ObjectMapper());
    }

    @Test
    @DisplayName("Publica los eventos pendientes del outbox y marca el envío")
    void publicarPendientes() throws Exception {
        EventoSaliente evento = EventoSaliente.builder()
                .id(UUID.randomUUID())
                .topic("unimag.m1.estado-actualizado.v1")
                .clave("key-1")
                .payload("{\"type\":\"estado.actualizado\"}")
                .headers("{\"X-Event-Type\":\"estado.actualizado\"}")
                .creadoEn(LocalDateTime.now())
                .build();

        when(eventoSalienteRepository.findTop50ByEnviadoEnIsNullOrderByCreadoEnAsc())
                .thenReturn(List.of(evento));

        relay.publicarPendientes();

        verify(estadoActualizadoProducer).enviar(
                eq("unimag.m1.estado-actualizado.v1"),
                eq("key-1"),
                eq("{\"type\":\"estado.actualizado\"}"),
                argThat((Map<String, String> headers) -> headers != null
                        && "estado.actualizado".equals(headers.get("X-Event-Type"))));
        verify(eventoSalienteRepository).save(evento);
        assertNotNull(evento.getEnviadoEn());
        assertNull(evento.getUltimoError());
    }
}
