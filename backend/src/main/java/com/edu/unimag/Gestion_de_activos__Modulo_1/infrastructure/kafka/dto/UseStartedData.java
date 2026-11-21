package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UseStartedData {
    @JsonAlias({"resource_id", "resourceCode", "resource_code", "codigoRecurso", "codigo_recurso"})
    private String resourceId;
    @JsonAlias({"resource_category", "categoriaRecurso", "categoria_recurso"})
    private String resourceCategory;
    @JsonAlias({"reservation_id", "idReserva", "id_reserva"})
    private String reservationId;
    private OccupancyWindow occupancy;
    @JsonAlias({"newStatus", "new_status", "new_state", "estadoNuevo", "estado_nuevo"})
    private String newState;
    @JsonAlias({"use_ends_at", "endsAt", "fechaFinUso", "fecha_fin_uso"})
    private Instant useEndsAt;
    private String reason;

    public UseStartedData() {
    }

    public UseStartedData(String resourceId, String resourceCategory, String reservationId, OccupancyWindow occupancy, String newState, String reason) {
        this.resourceId = resourceId;
        this.resourceCategory = resourceCategory;
        this.reservationId = reservationId;
        this.occupancy = occupancy;
        this.newState = newState;
        this.reason = reason;
    }

    public UseStartedData(UUID resourceId, String resourceCategory, String reservationId, OccupancyWindow occupancy, String newState, String reason) {
        this(resourceId == null ? null : resourceId.toString(), resourceCategory, reservationId, occupancy, newState, reason);
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setResourceId(UUID resourceId) {
        this.resourceId = resourceId == null ? null : resourceId.toString();
    }

    public String getResourceCategory() {
        return resourceCategory;
    }

    public void setResourceCategory(String resourceCategory) {
        this.resourceCategory = resourceCategory;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public OccupancyWindow getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(OccupancyWindow occupancy) {
        this.occupancy = occupancy;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    public Instant getUseEndsAt() {
        return useEndsAt;
    }

    public void setUseEndsAt(Instant useEndsAt) {
        this.useEndsAt = useEndsAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
