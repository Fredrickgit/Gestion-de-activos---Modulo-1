package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceEventPayload<T> {

    @JsonAlias({"eventoId", "id"})
    private String eventId;

    private String type;

    private String version;

    private OffsetDateTime timestamp;

    private String source;

    private T data;

    public ResourceEventPayload() {
    }

    public ResourceEventPayload(String eventId, String type, String version, OffsetDateTime timestamp, String source, T data) {
        this.eventId = eventId;
        this.type = type;
        this.version = version;
        this.timestamp = timestamp;
        this.source = source;
        this.data = data;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
