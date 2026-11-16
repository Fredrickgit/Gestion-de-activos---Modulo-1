package com.edu.unimag.Gestion_de_activos__Modulo_1.application.ports;

import java.util.Optional;
import java.util.UUID;

/**
 * Validaciones cruzadas de solo lectura contra M2 y M3.
 *
 * <p>{@link Optional#empty()} representa un fallo técnico o timeout; un valor
 * presente indica el resultado de la validación.</p>
 */
public interface ValidacionExternaPort {

    Optional<Boolean> tieneReservaActiva(UUID recursoId);

    Optional<Boolean> tieneNovedadPendiente(UUID recursoId);
}
