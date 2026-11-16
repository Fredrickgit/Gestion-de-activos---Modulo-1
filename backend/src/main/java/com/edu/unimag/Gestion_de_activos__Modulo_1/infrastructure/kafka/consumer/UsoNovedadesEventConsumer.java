package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.consumer;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.config.KafkaTopics;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.DamageReportData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.EndedReservationData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.KafkaHeaders;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.ResourceEventPayload;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.service.EventProcessingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Consumidor para tópicos originados en Módulo 3 (Uso y Novedades de Check-out / Daños).
 * Tópico: unimag.m3.uso-novedades.v1
 */
@Component
public class UsoNovedadesEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UsoNovedadesEventConsumer.class);

    private final EventProcessingService eventProcessingService;
    private final ObjectMapper objectMapper;

    public UsoNovedadesEventConsumer(EventProcessingService eventProcessingService, ObjectMapper objectMapper) {
        this.eventProcessingService = eventProcessingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.INBOUND_M3_USO_NOVEDADES,
            groupId = KafkaTopics.CONSUMER_GROUP_M1,
            containerFactory = "highPriorityKafkaListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) throws Exception {
        String key = record.key();
        String payload = record.value();
        String correlationId = extraerHeader(record, KafkaHeaders.CORRELATION_ID);
        String headerEventType = extraerHeader(record, KafkaHeaders.EVENT_TYPE);

        log.info("Mensaje de alta prioridad recibido de M3 [topic={}, partition={}, offset={}, key={}, corrId={}, type={}]",
                record.topic(), record.partition(), record.offset(), key, correlationId, headerEventType);

        try {
            String resolvedType = resolverTipoEvento(headerEventType, payload);

            if (KafkaHeaders.EVENT_TYPE_REPORTE_DANOS.equalsIgnoreCase(resolvedType) ||
                "DamageReport".equalsIgnoreCase(resolvedType)) {

                ResourceEventPayload<DamageReportData> damageEvent = objectMapper.readValue(
                        payload,
                        new TypeReference<ResourceEventPayload<DamageReportData>>() {}
                );
                eventProcessingService.procesarReporteDanos(damageEvent, payload);

            } else if (KafkaHeaders.EVENT_TYPE_RESERVA_FINALIZADA.equalsIgnoreCase(resolvedType) ||
                       "EndedReservation".equalsIgnoreCase(resolvedType)) {

                ResourceEventPayload<EndedReservationData> endedEvent = objectMapper.readValue(
                        payload,
                        new TypeReference<ResourceEventPayload<EndedReservationData>>() {}
                );
                eventProcessingService.procesarReservaFinalizada(endedEvent, payload);

            } else {
                log.warn("Tipo de evento no reconocido en tópico de novedades M3: '{}'. Intentando evaluar estructura.", resolvedType);
                if (payload.contains("id_novedad_m3") || payload.contains("DamageReport")) {
                    ResourceEventPayload<DamageReportData> damageEvent = objectMapper.readValue(
                            payload,
                            new TypeReference<ResourceEventPayload<DamageReportData>>() {}
                    );
                    eventProcessingService.procesarReporteDanos(damageEvent, payload);
                } else {
                    ResourceEventPayload<EndedReservationData> endedEvent = objectMapper.readValue(
                            payload,
                            new TypeReference<ResourceEventPayload<EndedReservationData>>() {}
                    );
                    eventProcessingService.procesarReservaFinalizada(endedEvent, payload);
                }
            }

            if (ack != null) {
                ack.acknowledge();
                log.debug("Offset confirmado para partición {} offset {}", record.partition(), record.offset());
            }
        } catch (Exception ex) {
            log.error("Error al procesar mensaje de M3 [key={}, offset={}]: {}",
                    key, record.offset(), ex.getMessage(), ex);
            // Se propaga la excepción original para que el DefaultErrorHandler
            // aplique correctamente las políticas de reintento/DLT.
            throw ex;
        }
    }

    private String resolverTipoEvento(String headerType, String payload) {
        if (headerType != null && !headerType.isBlank()) {
            return headerType.trim();
        }
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root.has("type")) {
                return root.get("type").asText();
            }
        } catch (Exception ignored) {
            // Fallback
        }
        return "";
    }

    private String extraerHeader(ConsumerRecord<String, String> record, String headerName) {
        org.apache.kafka.common.header.Header header = record.headers().lastHeader(headerName);
        if (header != null && header.value() != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }
}
