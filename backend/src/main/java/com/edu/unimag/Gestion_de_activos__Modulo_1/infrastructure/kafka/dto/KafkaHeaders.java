package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

public final class KafkaHeaders {

    private KafkaHeaders() {
        // Utility class
    }

    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String EVENT_TYPE = "X-Event-Type";
    public static final String SOURCE_MODULE = "X-Source-Module";
    public static final String EVENT_VERSION = "X-Event-Version";

    // Standard Event Types
    public static final String EVENT_TYPE_NOTIFICACION_USO = "notificacion.uso";
    public static final String EVENT_TYPE_RESERVA_FINALIZADA = "reserva.finalizada";
    public static final String EVENT_TYPE_REPORTE_DANOS = "reporte.danos";
    public static final String EVENT_TYPE_ESTADO_ACTUALIZADO = "estado.actualizado";
}

