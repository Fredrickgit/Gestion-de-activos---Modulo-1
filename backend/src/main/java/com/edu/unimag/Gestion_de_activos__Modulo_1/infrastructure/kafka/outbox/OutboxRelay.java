package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.outbox;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.jpa.EventoSalienteJpaRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.database.models.EventoSaliente;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.producer.EstadoActualizadoProducer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Relay del patrón Transactional Outbox: publica en Kafka los eventos
 * pendientes y marca su envío. La entrega es at-least-once; los consumidores
 * deduplican por {@code eventId}.
 */
@Component
@ConditionalOnProperty(name = "outbox.relay.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);
    private static final int MAX_LONGITUD_ERROR = 500;

    private final EventoSalienteJpaRepository eventoSalienteRepository;
    private final EstadoActualizadoProducer producer;
    private final ObjectMapper objectMapper;

    public OutboxRelay(EventoSalienteJpaRepository eventoSalienteRepository,
                       EstadoActualizadoProducer producer,
                       ObjectMapper objectMapper) {
        this.eventoSalienteRepository = eventoSalienteRepository;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.relay.delay-ms:5000}")
    public void publicarPendientes() {
        var pendientes = eventoSalienteRepository.findTop50ByEnviadoEnIsNullOrderByCreadoEnAsc();
        for (EventoSaliente evento : pendientes) {
            try {
                Map<String, String> headers = objectMapper.readValue(
                        evento.getHeaders(), new TypeReference<Map<String, String>>() {});
                producer.enviar(evento.getTopic(), evento.getClave(), evento.getPayload(), headers);
                evento.setEnviadoEn(LocalDateTime.now());
                evento.setUltimoError(null);
            } catch (Exception ex) {
                evento.setIntentos(evento.getIntentos() + 1);
                evento.setUltimoError(truncar(ex.getMessage()));
                log.error("No se pudo publicar el evento de outbox {} (intento {}): {}",
                        evento.getId(), evento.getIntentos(), ex.getMessage());
            }
            eventoSalienteRepository.save(evento);
        }
    }

    private String truncar(String mensaje) {
        if (mensaje == null) {
            return "Error desconocido";
        }
        return mensaje.length() <= MAX_LONGITUD_ERROR
                ? mensaje
                : mensaje.substring(0, MAX_LONGITUD_ERROR);
    }
}
