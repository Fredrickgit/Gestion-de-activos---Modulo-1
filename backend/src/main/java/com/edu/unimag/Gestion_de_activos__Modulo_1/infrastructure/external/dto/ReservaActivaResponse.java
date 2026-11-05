package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.dto;

import java.util.UUID;

public class ReservaActivaResponse {
    private UUID recursoId;
    private boolean tieneReservaActiva;

    public ReservaActivaResponse() {
    }

    public ReservaActivaResponse(UUID recursoId, boolean tieneReservaActiva) {
        this.recursoId = recursoId;
        this.tieneReservaActiva = tieneReservaActiva;
    }

    public UUID getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(UUID recursoId) {
        this.recursoId = recursoId;
    }

    public boolean isTieneReservaActiva() {
        return tieneReservaActiva;
    }

    public void setTieneReservaActiva(boolean tieneReservaActiva) {
        this.tieneReservaActiva = tieneReservaActiva;
    }
}
