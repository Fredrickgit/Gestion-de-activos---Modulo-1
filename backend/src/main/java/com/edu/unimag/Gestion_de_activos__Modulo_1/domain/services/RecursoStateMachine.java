package com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.EstadoSinCambioException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.TransicionEstadoInvalidaException;

/**
 * Reglas de transición del ciclo de vida de un recurso.
 *
 * <p>Es la única implementación de la máquina de estados del sistema: tanto la
 * actualización manual por REST como el consumo de eventos Kafka deben usarla
 * para evitar reglas divergentes.</p>
 */
public final class RecursoStateMachine {

    private RecursoStateMachine() {
        // Utility class
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
            // Un recurso disponible puede empezar a usarse, reservarse, bloquearse o
            // enviarse a mantenimiento.
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
            // Salir de mantenimiento exige una revisión técnica intermedia que no
            // puede resolverse con una transición directa a DISPONIBLE.
            case EN_MANTENIMIENTO -> false;
        };
    }
}
