package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.infrastructure.kafka.producer;

import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.producer.EstadoActualizadoProducer;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoActualizadoProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private EstadoActualizadoProducer producer;

    @BeforeEach
    void setUp() {
        producer = new EstadoActualizadoProducer(kafkaTemplate);
    }

    @Test
    @DisplayName("Publica el evento en el topico indicado")
    void enviarPublicaEvento() {
        SendResult sendResult = org.mockito.Mockito.mock(SendResult.class);
        RecordMetadata recordMetadata = org.mockito.Mockito.mock(RecordMetadata.class);
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(CompletableFuture.completedFuture(sendResult));

        producer.enviar("unimag.m1.estado-actualizado.v1", "key-1", "{\"type\":\"estado.actualizado\"}",
                Map.of("X-Event-Type", "estado.actualizado"));

        ArgumentCaptor<ProducerRecord> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());
        assertEquals("unimag.m1.estado-actualizado.v1", captor.getValue().topic());
    }

    @Test
    @DisplayName("Lanza error cuando falla la publicacion")
    void enviarFalla() {
        when(kafkaTemplate.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("broker down")));

        assertThrows(IllegalStateException.class, () ->
                producer.enviar("unimag.m1.estado-actualizado.v1", "key-1", "payload", null));
    }
}
