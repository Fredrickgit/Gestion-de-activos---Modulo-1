package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities.enums;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoDisponibilidadRecursoEnumTest {

    @Test
    @DisplayName("values devuelve las constantes en orden")
    void values_devuelveConstantesEnOrden() {
        assertThat(EstadoDisponibilidadRecurso.values())
                .containsExactly(
                        EstadoDisponibilidadRecurso.DISPONIBLE,
                        EstadoDisponibilidadRecurso.EN_USO,
                        EstadoDisponibilidadRecurso.EN_MANTENIMIENTO,
                        EstadoDisponibilidadRecurso.RESERVADO,
                        EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO);
    }

    @Test
    @DisplayName("valueOf resuelve cada constante")
    void valueOf_resuelveCadaConstante() {
        assertThat(EstadoDisponibilidadRecurso.valueOf("DISPONIBLE")).isEqualTo(EstadoDisponibilidadRecurso.DISPONIBLE);
        assertThat(EstadoDisponibilidadRecurso.valueOf("EN_USO")).isEqualTo(EstadoDisponibilidadRecurso.EN_USO);
        assertThat(EstadoDisponibilidadRecurso.valueOf("EN_MANTENIMIENTO")).isEqualTo(EstadoDisponibilidadRecurso.EN_MANTENIMIENTO);
        assertThat(EstadoDisponibilidadRecurso.valueOf("RESERVADO")).isEqualTo(EstadoDisponibilidadRecurso.RESERVADO);
        assertThat(EstadoDisponibilidadRecurso.valueOf("BLOQUEO_ACADEMICO")).isEqualTo(EstadoDisponibilidadRecurso.BLOQUEO_ACADEMICO);
    }

    @Test
    @DisplayName("valueOf con nombre inválido lanza IllegalArgumentException")
    void valueOf_nombreInvalido_lanzaExcepcion() {
        assertThatThrownBy(() -> EstadoDisponibilidadRecurso.valueOf("INVALIDO"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
