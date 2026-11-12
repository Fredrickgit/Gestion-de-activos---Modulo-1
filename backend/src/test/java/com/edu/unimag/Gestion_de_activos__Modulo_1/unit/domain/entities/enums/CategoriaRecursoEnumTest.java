package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities.enums;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoriaRecursoEnumTest {

    @Test
    @DisplayName("values devuelve las constantes en orden")
    void values_devuelveConstantesEnOrden() {
        assertThat(CategoriaRecurso.values())
                .containsExactly(CategoriaRecurso.ACTIVO, CategoriaRecurso.ESPACIO);
    }

    @Test
    @DisplayName("valueOf resuelve cada constante")
    void valueOf_resuelveCadaConstante() {
        assertThat(CategoriaRecurso.valueOf("ACTIVO")).isEqualTo(CategoriaRecurso.ACTIVO);
        assertThat(CategoriaRecurso.valueOf("ESPACIO")).isEqualTo(CategoriaRecurso.ESPACIO);
    }

    @Test
    @DisplayName("valueOf con nombre inválido lanza IllegalArgumentException")
    void valueOf_nombreInvalido_lanzaExcepcion() {
        assertThatThrownBy(() -> CategoriaRecurso.valueOf("INVALIDO"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
