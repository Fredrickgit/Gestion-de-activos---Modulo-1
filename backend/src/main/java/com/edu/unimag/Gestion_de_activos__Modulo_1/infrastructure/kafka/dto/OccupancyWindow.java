package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OccupancyWindow {
    private OffsetDateTime start;
    private OffsetDateTime end;

    public OccupancyWindow() {
    }

    public OccupancyWindow(OffsetDateTime start, OffsetDateTime end) {
        this.start = start;
        this.end = end;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }
}
