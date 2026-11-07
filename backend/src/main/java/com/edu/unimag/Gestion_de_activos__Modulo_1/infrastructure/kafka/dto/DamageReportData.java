package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DamageReportData {
    private UUID resourceId;

    @JsonProperty("recourseCategory")
    private String resourceCategory;

    @JsonProperty("id_reservation_m2")
    private String idReservationM2;

    @JsonProperty("id_novedad_m3")
    private String idNovedadM3;

    private StudentInfo student;

    private String description;

    @JsonProperty("physical_state")
    private String physicalState;

    public DamageReportData() {
    }

    public DamageReportData(UUID resourceId, String resourceCategory, String idReservationM2, String idNovedadM3, StudentInfo student, String description, String physicalState) {
        this.resourceId = resourceId;
        this.resourceCategory = resourceCategory;
        this.idReservationM2 = idReservationM2;
        this.idNovedadM3 = idNovedadM3;
        this.student = student;
        this.description = description;
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

    public String getIdNovedadM3() {
        return idNovedadM3;
    }

    public void setIdNovedadM3(String idNovedadM3) {
        this.idNovedadM3 = idNovedadM3;
    }

    public StudentInfo getStudent() {
        return student;
    }

    public void setStudent(StudentInfo student) {
        this.student = student;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhysicalState() {
        return physicalState;
    }

    public void setPhysicalState(String physicalState) {
        this.physicalState = physicalState;
    }
}
