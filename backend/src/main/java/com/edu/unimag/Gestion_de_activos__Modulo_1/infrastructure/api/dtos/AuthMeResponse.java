package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

/**
 * Usuario autenticado y su rol, usado por el frontend para mostrar la sesión.
 *
 * <p>El rol se devuelve sin el prefijo {@code ROLE_} de Spring Security, por
 * ejemplo {@code DIRECCION} o {@code MONITOR}.</p>
 */
public record AuthMeResponse(
        String usuario,
        String rol
) {
}
