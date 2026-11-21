package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.services;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.services.RecursoStateMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecursoStateMachineTest {

    @Test
    void soloContieneEstadosOperativosDelModuloUno() {
        assertTrue(EstadoDisponibilidadRecurso.values().length == 3);
    }

    @Test
    void permiteTransicionesOperativasDefinidas() {
        assertTrue(RecursoStateMachine.esTransicionPermitida(
                EstadoDisponibilidadRecurso.DISPONIBLE, EstadoDisponibilidadRecurso.EN_USO));
        assertTrue(RecursoStateMachine.esTransicionPermitida(
                EstadoDisponibilidadRecurso.DISPONIBLE, EstadoDisponibilidadRecurso.EN_MANTENIMIENTO));
        assertTrue(RecursoStateMachine.esTransicionPermitida(
                EstadoDisponibilidadRecurso.EN_USO, EstadoDisponibilidadRecurso.DISPONIBLE));
        assertTrue(RecursoStateMachine.esTransicionPermitida(
                EstadoDisponibilidadRecurso.EN_USO, EstadoDisponibilidadRecurso.EN_MANTENIMIENTO));
    }

    @Test
    void rechazaTransicionesNoPermitidas() {
        for (EstadoDisponibilidadRecurso actual : EstadoDisponibilidadRecurso.values()) {
            assertFalse(RecursoStateMachine.esTransicionPermitida(actual, actual));
        }
        assertFalse(RecursoStateMachine.esTransicionPermitida(
                EstadoDisponibilidadRecurso.EN_MANTENIMIENTO, EstadoDisponibilidadRecurso.DISPONIBLE));
        assertFalse(RecursoStateMachine.esTransicionPermitida(
                EstadoDisponibilidadRecurso.EN_MANTENIMIENTO, EstadoDisponibilidadRecurso.EN_USO));
    }
}
