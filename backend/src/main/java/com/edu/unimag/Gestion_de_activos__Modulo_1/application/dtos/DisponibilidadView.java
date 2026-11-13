package com.edu.unimag.Gestion_de_activos__Modulo_1.application.dtos;

import java.util.UUID;

public record DisponibilidadView(
        UUID id,
        String estado,
        boolean disponible,
        HorarioOcupacionView horarioAsignado,
        String motivoNoDisponibilidad
) {
}
