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

        return switch (nuevo) {
            case EN_USO -> actual == EstadoDisponibilidadRecurso.DISPONIBLE
                    || actual == EstadoDisponibilidadRecurso.RESERVADO;
            case DISPONIBLE -> actual == EstadoDisponibilidadRecurso.EN_USO
                    || actual == EstadoDisponibilidadRecurso.EN_MANTENIMIENTO
                    || actual == EstadoDisponibilidadRecurso.RESERVADO
                    || actual == EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO;
            case EN_MANTENIMIENTO -> true;
            case RESERVADO, BLOQUEO_ACADEMICO -> actual == EstadoDisponibilidadRecurso.DISPONIBLE;
        };
    }
}
