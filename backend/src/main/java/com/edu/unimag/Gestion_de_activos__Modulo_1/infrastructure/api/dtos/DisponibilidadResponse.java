package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.dtos;

import java.util.UUID;

/**
 * Respuesta de GET /api/v1/recursos/{id}/disponibilidad.
 */
public record DisponibilidadResponse(
        UUID id,
        String estado,
        boolean disponible,
        HorarioOcupacionResponse horarioAsignado,
        String motivoNoDisponibilidad
) {
}
