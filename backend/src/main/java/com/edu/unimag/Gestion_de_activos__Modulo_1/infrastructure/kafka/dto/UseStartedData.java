package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UseStartedData {
    private UUID resourceId;
    private String resourceCategory;
    private String reservationId;
    private OccupancyWindow occupancy;
    private String newState;
    private String reason;

    public UseStartedData() {
    }

    public UseStartedData(UUID resourceId, String resourceCategory, String reservationId, OccupancyWindow occupancy, String newState, String reason) {
        this.resourceId = resourceId;
        this.resourceCategory = resourceCategory;
        this.reservationId = reservationId;
        this.occupancy = occupancy;
        this.newState = newState;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
