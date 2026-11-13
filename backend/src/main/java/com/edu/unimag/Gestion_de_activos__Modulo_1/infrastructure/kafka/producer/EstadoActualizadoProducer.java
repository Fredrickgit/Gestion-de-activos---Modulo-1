package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.producer;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.config.KafkaTopics;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.EstadoActualizadoEvent;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.KafkaHeaders;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Productor Kafka para emitir el evento 'estado.actualizado' hacia M2 y M3
 * cuando ocurre una actualización manual de estado en M1.
 */
@Component
public class EstadoActualizadoProducer {

    private static final Logger log = LoggerFactory.getLogger(EstadoActualizadoProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EstadoActualizadoProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEstadoActualizado(
            String correlationId,
            UUID recursoId,
            String categoriaRecurso,
            String estadoAnterior,
            String nuevoEstado,
            String motivo,
            String usuarioResponsable) {

        String corrId = (correlationId != null && !correlationId.isBlank())
                ? correlationId
                : "corr-" + UUID.randomUUID();

        String eventId = "evt-" + UUID.randomUUID();

        EstadoActualizadoEvent.DataPayload dataPayload = new EstadoActualizadoEvent.DataPayload(
                recursoId,
                categoriaRecurso,
                estadoAnterior,
                nuevoEstado,
                motivo,
                usuarioResponsable
        );

        EstadoActualizadoEvent event = new EstadoActualizadoEvent(
                eventId,
                KafkaHeaders.EVENT_TYPE_ESTADO_ACTUALIZADO,
                "1.0",
                OffsetDateTime.now(),
                "MODULO_1",
                dataPayload
        );

        String key = recursoId.toString();
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                KafkaTopics.OUTBOUND_ESTADO_ACTUALIZADO,
                key,
                event
        );

        record.headers().add(KafkaHeaders.CORRELATION_ID, corrId.getBytes(StandardCharsets.UTF_8));
        record.headers().add(KafkaHeaders.EVENT_TYPE, KafkaHeaders.EVENT_TYPE_ESTADO_ACTUALIZADO.getBytes(StandardCharsets.UTF_8));
        record.headers().add(KafkaHeaders.SOURCE_MODULE, "MODULO_1".getBytes(StandardCharsets.UTF_8));
        record.headers().add(KafkaHeaders.EVENT_VERSION, "1.0".getBytes(StandardCharsets.UTF_8));

        log.info("Publicando evento {} en tópico {} con key {}: eventId={}",
                KafkaHeaders.EVENT_TYPE_ESTADO_ACTUALIZADO, KafkaTopics.OUTBOUND_ESTADO_ACTUALIZADO, key, eventId);

        kafkaTemplate.send(record).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Fallo al enviar evento {} para recurso {}: {}", eventId, recursoId, ex.getMessage(), ex);
            } else {
                log.debug("Evento {} enviado exitosamente al offset {}", eventId, result.getRecordMetadata().offset());
            }
        });
    }
}
