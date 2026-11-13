package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.consumer;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.config.KafkaTopics;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.KafkaHeaders;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.ResourceEventPayload;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.UseStartedData;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.service.EventProcessingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Consumidor para tópicos originados en Módulo 2 (Reservas y Ocupación).
 * Tópico: unimag.m2.reservas-eventos.v1
 */
@Component
public class ReservasEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReservasEventConsumer.class);

    private final EventProcessingService eventProcessingService;
    private final ObjectMapper objectMapper;

    public ReservasEventConsumer(EventProcessingService eventProcessingService, ObjectMapper objectMapper) {
        this.eventProcessingService = eventProcessingService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = KafkaTopics.INBOUND_M2_RESERVAS,
            groupId = KafkaTopics.CONSUMER_GROUP_M1,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String key = record.key();
        String payload = record.value();
        String correlationId = extraerHeader(record, KafkaHeaders.CORRELATION_ID);
        String eventType = extraerHeader(record, KafkaHeaders.EVENT_TYPE);

        log.info("Mensaje recibido de M2 [topic={}, partition={}, offset={}, key={}, corrId={}, type={}]",
                record.topic(), record.partition(), record.offset(), key, correlationId, eventType);

        try {
            ResourceEventPayload<UseStartedData> event = objectMapper.readValue(
                    payload,
                    new TypeReference<ResourceEventPayload<UseStartedData>>() {}
            );

            eventProcessingService.procesarNotificacionUso(event, payload);

            // Commit manual a Kafka únicamente tras procesar exitosamente la transacción DB
            if (ack != null) {
                ack.acknowledge();
                log.debug("Offset confirmado para partición {} offset {}", record.partition(), record.offset());
            }
        } catch (Exception ex) {
            log.error("Error al procesar mensaje de M2 [key={}, offset={}]: {}", key, record.offset(), ex.getMessage());
            throw new RuntimeException("Fallo en procesamiento de evento de reservas M2", ex);
        }
    }

    private String extraerHeader(ConsumerRecord<String, String> record, String headerName) {
        org.apache.kafka.common.header.Header header = record.headers().lastHeader(headerName);
        if (header != null && header.value() != null) {
            return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }
}
