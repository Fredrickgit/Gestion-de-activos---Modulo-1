package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.config;

public final class KafkaTopics {

    private KafkaTopics() {
        // Utility class
    }

    public static final String INBOUND_M2_RESERVAS = "unimag.m2.reservas-eventos.v1";
    public static final String INBOUND_M3_USO_NOVEDADES = "unimag.m3.uso-novedades.v1";
    public static final String OUTBOUND_ESTADO_ACTUALIZADO = "unimag.m1.estado-actualizado.v1";
    public static final String DLT_TOPIC = "unimag.m1.inbox.DLT";

    public static final String CONSUMER_GROUP_M1 = "m1-activos-consumer-group";
}

