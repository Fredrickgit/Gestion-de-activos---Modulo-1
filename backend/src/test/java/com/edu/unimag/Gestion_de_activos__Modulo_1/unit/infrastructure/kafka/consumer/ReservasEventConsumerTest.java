package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.kafka.consumer;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.config.KafkaTopics;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.KafkaHeaders;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.ResourceEventPayload;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.UseStartedData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.consumer.ReservasEventConsumer;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.service.EventProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservasEventConsumerTest {

    @Mock
    private EventProcessingService eventProcessingService;

    @Mock
    private Acknowledgment ack;

    private ReservasEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ReservasEventConsumer(eventProcessingService, new ObjectMapper().findAndRegisterModules());
    }

    @Test
    @DisplayName("Procesa notificacion de uso del modulo 2")
    void procesarNotificacionUso() throws Exception {
        String payload = "{\"eventId\":\"evt-9\",\"type\":\"notificacion.uso\",\"data\":{\"resourceId\":\"00000000-0000-0000-0000-000000000002\",\"resourceCategory\":\"ACTIVO\"}}";
        ConsumerRecord<String, String> record = mockRecord(payload, "notificacion.uso");

        consumer.onMessage(record, ack);

        verify(eventProcessingService).procesarNotificacionUso(
                org.mockito.ArgumentMatchers.<ResourceEventPayload<UseStartedData>>any(), eq(payload));
        verify(ack).acknowledge();
    }

    @Test
    void procesaResourceUseStartedAunqueLaVersionNoSeaExactamenteUno() throws Exception {
        String payload = """
                {
                  "eventId": "c9b2f536-8a41-4d07-95ec-3f1a0d7b2e58",
                  "type": "ResourceUseStarted",
                  "version": "1.0",
                  "occurredAt": "2026-11-20T13:21:10-05:00",
                  "data": {
                    "resourceId": "65e72b13-2fd4-47b0-9439-5a78be89de26",
                    "resourceCategory": "ESPACIO",
                    "newStatus": "EN_USO",
                    "useEndsAt": "2026-11-20T14:00:00-05:00"
                  }
                }
                """;

        consumer.onMessage(new ConsumerRecord<>(KafkaTopics.INBOUND_M2_RESERVAS, 0, 0,
                "539925fb-2346-4212-bf05-b867f1e929ff", payload), ack);

        verify(eventProcessingService).procesarNotificacionUso(
                org.mockito.ArgumentMatchers.<ResourceEventPayload<UseStartedData>>any(), eq(payload));
        verify(ack).acknowledge();
    }

    @Test
    void procesaContratoV1ConFechaDeFinYVersionNumerica() throws Exception {
        String payload = """
                {
                  "eventId": "c9b2f536-8a41-4d07-95ec-3f1a0d7b2e58",
                  "type": "ResourceUseStarted",
                  "version": 1,
                  "occurredAt": "2026-11-20T13:21:10-05:00",
                  "source": "MODULE_2",
                  "data": {
                    "resourceId": "65e72b13-2fd4-47b0-9439-5a78be89de26",
                    "resourceCategory": "ESPACIO",
                    "newStatus": "EN_USO",
                    "useEndsAt": "2026-11-20T14:00:00-05:00"
                  }
                }
                """;

        consumer.onMessage(new ConsumerRecord<>(KafkaTopics.INBOUND_M2_RESERVAS, 0, 0,
                "539925fb-2346-4212-bf05-b867f1e929ff", payload), ack);

        verify(eventProcessingService).procesarNotificacionUso(argThat(event ->
                Instant.parse("2026-11-20T18:21:10Z").equals(event.getTimestamp().toInstant())
                        && "EN_USO".equals(event.getData().getNewState())
                        && Instant.parse("2026-11-20T19:00:00Z").equals(event.getData().getUseEndsAt())),
                eq(payload));
        verify(ack).acknowledge();
    }

    private ConsumerRecord<String, String> mockRecord(String payload, String eventType) {
        ConsumerRecord<String, String> record = org.mockito.Mockito.mock(ConsumerRecord.class);
        when(record.key()).thenReturn("key-1");
        when(record.value()).thenReturn(payload);
        when(record.topic()).thenReturn(KafkaTopics.INBOUND_M2_RESERVAS);
        when(record.partition()).thenReturn(0);
        when(record.offset()).thenReturn(1L);

        Headers headers = org.mockito.Mockito.mock(Headers.class);
        Header correlation = org.mockito.Mockito.mock(Header.class);
        when(correlation.value()).thenReturn("corr-2".getBytes(StandardCharsets.UTF_8));
        when(headers.lastHeader(KafkaHeaders.CORRELATION_ID)).thenReturn(correlation);
        Header type = org.mockito.Mockito.mock(Header.class);
        when(type.value()).thenReturn(eventType.getBytes(StandardCharsets.UTF_8));
        when(headers.lastHeader(KafkaHeaders.EVENT_TYPE)).thenReturn(type);
        when(record.headers()).thenReturn(headers);

        return record;
    }
}
