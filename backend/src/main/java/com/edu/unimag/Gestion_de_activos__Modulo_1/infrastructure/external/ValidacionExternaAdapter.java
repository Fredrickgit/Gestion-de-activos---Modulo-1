package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports.ValidacionExternaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de validaciones cruzadas contra M2 (reservas) y M3 (novedades).
 */
@Component
public class ValidacionExternaAdapter implements ValidacionExternaPort {

    private final Modulo2Client modulo2Client;
    private final Modulo3Client modulo3Client;

    @Value("${external.validation.enabled:true}")
    private boolean validacionExternaHabilitada;

    public ValidacionExternaAdapter(Modulo2Client modulo2Client, Modulo3Client modulo3Client) {
        this.modulo2Client = modulo2Client;
        this.modulo3Client = modulo3Client;
    }

    @Override
    public Optional<Boolean> tieneReservaActiva(UUID recursoId) {
        if (!validacionExternaHabilitada) {
            return Optional.of(false);
        }
        return modulo2Client.consultarReservaActiva(recursoId)
                .map(reserva -> reserva.isTieneReservaActiva());
    }

    @Override
    public Optional<Boolean> tieneNovedadPendiente(UUID recursoId) {
        if (!validacionExternaHabilitada) {
            return Optional.of(false);
        }
        return modulo3Client.consultarNovedadPendiente(recursoId)
                .map(novedad -> novedad.isTieneNovedadPendiente());
    }
}
