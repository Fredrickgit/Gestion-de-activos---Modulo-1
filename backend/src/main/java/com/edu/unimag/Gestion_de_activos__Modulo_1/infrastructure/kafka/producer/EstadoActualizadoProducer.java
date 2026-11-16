package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Envía a Kafka los payloads JSON (String) construidos por el outbox.
 */
@Component
public class EstadoActualizadoProducer {

    private static final Logger log = LoggerFactory.getLogger(EstadoActualizadoProducer.class);
    private static final long SEND_TIMEOUT_SECONDS = 10L;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public EstadoActualizadoProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publica de forma síncrona un evento. Lanza excepción si el broker no
     * confirma la escritura para que el relay mantenga la fila pendiente.
     */
    public void enviar(String topic, String key, String payload, Map<String, String> headers) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);
        if (headers != null) {
            headers.forEach((nombre, valor) -> {
                if (nombre != null && valor != null) {
                    record.headers().add(nombre, valor.getBytes(StandardCharsets.UTF_8));
                }
            });
        }

        try {
            var metadata = kafkaTemplate.send(record).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.debug("Evento enviado a {}-{} offset {}", topic,
                    metadata.getRecordMetadata().partition(), metadata.getRecordMetadata().offset());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Envío a Kafka interrumpido para el tópico " + topic, ex);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo publicar el evento en el tópico " + topic, ex);
        }
    }
}
