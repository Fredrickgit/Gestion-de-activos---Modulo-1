package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.service;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.api.middlewares.exceptions.TransicionEstadoInvalidaException;

/**
 * Máquina de estados de disponibilidad de un recurso.
 *
 * <p>Se mantiene alineada con las transiciones aceptadas por
 * {@code EventProcessingService} para que la actualización manual (REST) y la
 * actualización por eventos Kafka no diverjan.</p>
 */
public final class RecursoStateMachine {

    private RecursoStateMachine() {
        // Utility class
    }

    public static void validarTransicion(EstadoDisponibilidadRecurso actual,
                                         EstadoDisponibilidadRecurso nuevo) {
        if (actual == nuevo) {
            throw new TransicionEstadoInvalidaException(
                    "El recurso ya se encuentra en el estado " + actual + ".");
        }

        switch (nuevo) {
            case EN_USO -> {
                if (actual != EstadoDisponibilidadRecurso.DISPONIBLE
                        && actual != EstadoDisponibilidadRecurso.RESERVADO) {
                    throw new TransicionEstadoInvalidaException(
                            "Transición inválida: no se puede pasar de " + actual + " a " + nuevo + ".");
                }
            }
            case DISPONIBLE -> {
                if (actual != EstadoDisponibilidadRecurso.EN_USO
                        && actual != EstadoDisponibilidadRecurso.EN_MANTENIMIENTO
                        && actual != EstadoDisponibilidadRecurso.RESERVADO
                        && actual != EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO) {
                    throw new TransicionEstadoInvalidaException(
                            "Transición inválida: no se puede pasar de " + actual + " a " + nuevo + ".");
                }
            }
            case EN_MANTENIMIENTO -> {
                // Cualquier estado puede pasar a mantenimiento.
            }
            case RESERVADO, BLOQUEO_ACADEMICO -> {
                if (actual != EstadoDisponibilidadRecurso.DISPONIBLE) {
                    throw new TransicionEstadoInvalidaException(
                            "Transición inválida: no se puede pasar de " + actual + " a " + nuevo + ".");
                }
            }
        }
    }
}
