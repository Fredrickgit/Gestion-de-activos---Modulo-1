package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EstadoActualizadoEvent {

    private String eventId;
    private String type;
    private String version;
    private OffsetDateTime timestamp;
    private String source;
    private DataPayload data;

    public EstadoActualizadoEvent() {
    }

    public EstadoActualizadoEvent(String eventId, String type, String version, OffsetDateTime timestamp, String source, DataPayload data) {
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

    public DataPayload getData() {
        return data;
    }

    public void setData(DataPayload data) {
        this.data = data;
    }

    public static class DataPayload {
        private UUID resourceId;
        private String resourceCategory;
        private String previousState;
        private String newState;
        private String reason;
        private String updatedBy;

        public DataPayload() {
        }

        public DataPayload(UUID resourceId, String resourceCategory, String previousState, String newState, String reason, String updatedBy) {
            this.resourceId = resourceId;
            this.resourceCategory = resourceCategory;
            this.previousState = previousState;
            this.newState = newState;
            this.reason = reason;
            this.updatedBy = updatedBy;
        }

        public UUID getResourceId() {
            return resourceId;
        }

        public void setResourceId(UUID resourceId) {
            this.resourceId = resourceId;
        }

        public String getResourceCategory() {
            return resourceCategory;
        }

        public void setResourceCategory(String resourceCategory) {
            this.resourceCategory = resourceCategory;
        }

        public String getPreviousState() {
            return previousState;
        }

        public void setPreviousState(String previousState) {
            this.previousState = previousState;
        }

        public String getNewState() {
            return newState;
        }

        public void setNewState(String newState) {
            this.newState = newState;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
    }
}
