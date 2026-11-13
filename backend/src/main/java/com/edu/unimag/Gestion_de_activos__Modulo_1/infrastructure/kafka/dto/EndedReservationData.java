package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EndedReservationData {
    private UUID resourceId;
    private String resourceCategory;

    @JsonProperty("id_reservation_m2")
    private String idReservationM2;

    @JsonProperty("id_checkout_m3")
    private String idCheckoutM3;

    @JsonProperty("realcheckoutdate")
    private OffsetDateTime realCheckoutDate;

    @JsonProperty("physical_state")
    private String physicalState;

    public EndedReservationData() {
    }

    public EndedReservationData(UUID resourceId, String resourceCategory, String idReservationM2, String idCheckoutM3, OffsetDateTime realCheckoutDate, String physicalState) {
        this.resourceId = resourceId;
        this.resourceCategory = resourceCategory;
        this.idReservationM2 = idReservationM2;
        this.idCheckoutM3 = idCheckoutM3;
        this.realCheckoutDate = realCheckoutDate;
        this.physicalState = physicalState;
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

    public String getIdReservationM2() {
        return idReservationM2;
    }

    public void setIdReservationM2(String idReservationM2) {
        this.idReservationM2 = idReservationM2;
    }

    public String getIdCheckoutM3() {
        return idCheckoutM3;
    }

    public void setIdCheckoutM3(String idCheckoutM3) {
        this.idCheckoutM3 = idCheckoutM3;
    }

    public OffsetDateTime getRealCheckoutDate() {
        return realCheckoutDate;
    }

    public void setRealCheckoutDate(OffsetDateTime realCheckoutDate) {
        this.realCheckoutDate = realCheckoutDate;
    }

    public String getPhysicalState() {
        return physicalState;
    }

    public void setPhysicalState(String physicalState) {
        this.physicalState = physicalState;
    }
}
