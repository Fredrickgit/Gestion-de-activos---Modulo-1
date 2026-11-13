package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.dto;

import java.util.UUID;

public class NovedadPendienteResponse {
    private UUID recursoId;
    private boolean tieneNovedadPendiente;

    public NovedadPendienteResponse() {
    }

    public NovedadPendienteResponse(UUID recursoId, boolean tieneNovedadPendiente) {
        this.recursoId = recursoId;
        this.tieneNovedadPendiente = tieneNovedadPendiente;
    }

    public UUID getRecursoId() {
        return recursoId;
    }

    public void setRecursoId(UUID recursoId) {
        this.recursoId = recursoId;
    }

    public boolean isTieneNovedadPendiente() {
        return tieneNovedadPendiente;
    }

    public void setTieneNovedadPendiente(boolean tieneNovedadPendiente) {
        this.tieneNovedadPendiente = tieneNovedadPendiente;
    }
}
