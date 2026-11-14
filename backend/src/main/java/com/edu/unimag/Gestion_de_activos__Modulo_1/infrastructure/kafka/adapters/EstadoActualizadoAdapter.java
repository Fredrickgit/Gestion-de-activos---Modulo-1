package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.adapters;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.EstadoActualizadoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EventoSalienteJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EventoSaliente;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.config.KafkaTopics;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.EstadoActualizadoEvent;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto.KafkaHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Adaptador que persiste el evento {@code estado.actualizado} en el outbox.
 *
 * <p>La fila se escribe dentro de la misma transacción que el cambio de estado,
 * por lo que el evento y el dato quedan atómicamente consistentes. Un relay
 * programado (ver {@code OutboxRelay}) lo publica después en Kafka.</p>
 */
@Component
public class EstadoActualizadoAdapter implements EstadoActualizadoPort {

    private final EventoSalienteJpaRepository eventoSalienteRepository;
    private final ObjectMapper objectMapper;

    public EstadoActualizadoAdapter(EventoSalienteJpaRepository eventoSalienteRepository,
                                    ObjectMapper objectMapper) {
        this.eventoSalienteRepository = eventoSalienteRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publicar(String correlationId,
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

        EstadoActualizadoEvent event = new EstadoActualizadoEvent(
                eventId,
                KafkaHeaders.EVENT_TYPE_ESTADO_ACTUALIZADO,
                "1.0",
                OffsetDateTime.now(),
                "MODULO_1",
                new EstadoActualizadoEvent.DataPayload(
                        recursoId,
                        categoriaRecurso,
                        estadoAnterior,
                        nuevoEstado,
                        motivo,
                        usuarioResponsable));

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(KafkaHeaders.CORRELATION_ID, corrId);
        headers.put(KafkaHeaders.EVENT_TYPE, KafkaHeaders.EVENT_TYPE_ESTADO_ACTUALIZADO);
        headers.put(KafkaHeaders.SOURCE_MODULE, "MODULO_1");
        headers.put(KafkaHeaders.EVENT_VERSION, "1.0");

        EventoSaliente saliente = EventoSaliente.builder()
                .id(UUID.randomUUID())
                .topic(KafkaTopics.OUTBOUND_ESTADO_ACTUALIZADO)
                .clave(recursoId.toString())
                .payload(serializar(event))
                .headers(serializar(headers))
                .creadoEn(LocalDateTime.now())
                .intentos(0)
                .build();

        eventoSalienteRepository.save(saliente);
    }

    private String serializar(Object valor) {
        try {
            return objectMapper.writeValueAsString(valor);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("No se pudo serializar el evento estado.actualizado.", ex);
        }
    }
}
