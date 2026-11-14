package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.EstadoSinCambioException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.TransicionEstadoInvalidaException;

public final class RecursoStateMachine {

    private RecursoStateMachine() {
    }

    public static void validarTransicion(EstadoDisponibilidadRecurso actual,
                                         EstadoDisponibilidadRecurso nuevo) {
        if (actual == null || nuevo == null) {
            throw new TransicionEstadoInvalidaException("El estado actual y el nuevo estado son obligatorios.");
        }
        if (actual == nuevo) {
            throw new EstadoSinCambioException(
                    "El recurso ya se encuentra en el estado " + actual + ".");
        }
        if (!esTransicionPermitida(actual, nuevo)) {
            throw new TransicionEstadoInvalidaException(
                    "Transición inválida: no se puede pasar de " + actual + " a " + nuevo + ".");
        }
    }

    public static boolean esTransicionPermitida(EstadoDisponibilidadRecurso actual,
                                                EstadoDisponibilidadRecurso nuevo) {
        if (actual == null || nuevo == null || actual == nuevo) {
            return false;
        }

        return switch (actual) {
            case DISPONIBLE -> nuevo == EstadoDisponibilidadRecurso.EN_USO
                    || nuevo == EstadoDisponibilidadRecurso.RESERVADO
                    || nuevo == EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO
                    || nuevo == EstadoDisponibilidadRecurso.EN_MANTENIMIENTO;
            // Al finalizar el uso solo puede liberarse o pasar a mantenimiento.
            case EN_USO -> nuevo == EstadoDisponibilidadRecurso.DISPONIBLE
                    || nuevo == EstadoDisponibilidadRecurso.EN_MANTENIMIENTO;
            case RESERVADO -> nuevo == EstadoDisponibilidadRecurso.DISPONIBLE
                    || nuevo == EstadoDisponibilidadRecurso.EN_USO
                    || nuevo == EstadoDisponibilidadRecurso.EN_MANTENIMIENTO;
            case BLOQUEO_ACADEMICO -> nuevo == EstadoDisponibilidadRecurso.DISPONIBLE;
            case EN_MANTENIMIENTO -> false;
        };
    }
}
