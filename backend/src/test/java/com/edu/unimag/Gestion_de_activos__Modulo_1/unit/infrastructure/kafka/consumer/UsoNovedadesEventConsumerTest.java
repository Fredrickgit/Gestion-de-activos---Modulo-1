package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.kafka.consumer;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.DamageReportData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.KafkaHeaders;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.consumer.UsoNovedadesEventConsumer;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.ResourceEventPayload;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.service.EventProcessingService;
import com.fasterxml.jackson.core.type.TypeReference;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsoNovedadesEventConsumerTest {

    @Mock
    private EventProcessingService eventProcessingService;

    @Mock
    private Acknowledgment ack;

    private UsoNovedadesEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new UsoNovedadesEventConsumer(eventProcessingService, new ObjectMapper());
    }

    @Test
    @DisplayName("Procesa reporte de danos del modulo 3")
    void procesarReporteDanos() throws Exception {
        String payload = "{\"eventId\":\"evt-1\",\"type\":\"reporte.danos\",\"data\":{\"resourceId\":\"00000000-0000-0000-0000-000000000001\",\"resourceCategory\":\"ACTIVO\"}}";
        ConsumerRecord<String, String> record = mockRecord(payload, "reporte.danos");

        consumer.onMessage(record, ack);

        verify(eventProcessingService).procesarReporteDanos(
                org.mockito.ArgumentMatchers.<ResourceEventPayload<DamageReportData>>any(), eq(payload));
        verify(ack).acknowledge();
    }

    private ConsumerRecord<String, String> mockRecord(String payload, String eventType) {
        ConsumerRecord<String, String> record = org.mockito.Mockito.mock(ConsumerRecord.class);
        when(record.key()).thenReturn("key-1");
        when(record.value()).thenReturn(payload);
        when(record.topic()).thenReturn("unimag.m3.uso-novedades.v1");
        when(record.partition()).thenReturn(0);
        when(record.offset()).thenReturn(1L);

        Headers headers = org.mockito.Mockito.mock(Headers.class);
        Header correlation = org.mockito.Mockito.mock(Header.class);
        when(correlation.value()).thenReturn("corr-1".getBytes(StandardCharsets.UTF_8));
        when(headers.lastHeader(KafkaHeaders.CORRELATION_ID)).thenReturn(correlation);
        Header type = org.mockito.Mockito.mock(Header.class);
        when(type.value()).thenReturn(eventType.getBytes(StandardCharsets.UTF_8));
        when(headers.lastHeader(KafkaHeaders.EVENT_TYPE)).thenReturn(type);
        when(record.headers()).thenReturn(headers);

        return record;
    }
}
