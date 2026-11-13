package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.adapters;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.EstadoActualizadoPort;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.producer.EstadoActualizadoProducer;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptador que publica el evento {@code estado.actualizado} en Kafka.
 */
@Component
public class EstadoActualizadoAdapter implements EstadoActualizadoPort {

    private final EstadoActualizadoProducer producer;

    public EstadoActualizadoAdapter(EstadoActualizadoProducer producer) {
        this.producer = producer;
    }

    @Override
    public void publicar(String correlationId,
                         UUID recursoId,
                         String categoriaRecurso,
                         String estadoAnterior,
                         String nuevoEstado,
                         String motivo,
                         String usuarioResponsable) {
        producer.publicarEstadoActualizado(
                correlationId,
                recursoId,
                categoriaRecurso,
                estadoAnterior,
                nuevoEstado,
                motivo,
                usuarioResponsable);
    }
}
