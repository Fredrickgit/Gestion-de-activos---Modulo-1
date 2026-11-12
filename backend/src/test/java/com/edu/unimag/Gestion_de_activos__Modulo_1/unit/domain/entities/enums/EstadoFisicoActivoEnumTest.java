package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities.enums;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFisicoActivoEnumTest {

    @Test
    @DisplayName("values devuelve las constantes en orden")
    void values_devuelveConstantesEnOrden() {
        assertThat(EstadoFisicoActivo.values())
                .containsExactly(
                        EstadoFisicoActivo.NUEVO,
                        EstadoFisicoActivo.OPTIMO,
                        EstadoFisicoActivo.MAL_ESTADO,
                        EstadoFisicoActivo.AVERIADO);
    }

    @Test
    @DisplayName("valueOf resuelve cada constante")
    void valueOf_resuelveCadaConstante() {
        assertThat(EstadoFisicoActivo.valueOf("NUEVO")).isEqualTo(EstadoFisicoActivo.NUEVO);
        assertThat(EstadoFisicoActivo.valueOf("OPTIMO")).isEqualTo(EstadoFisicoActivo.OPTIMO);
        assertThat(EstadoFisicoActivo.valueOf("MAL_ESTADO")).isEqualTo(EstadoFisicoActivo.MAL_ESTADO);
        assertThat(EstadoFisicoActivo.valueOf("AVERIADO")).isEqualTo(EstadoFisicoActivo.AVERIADO);
    }

    @Test
    @DisplayName("valueOf con nombre inválido lanza IllegalArgumentException")
    void valueOf_nombreInvalido_lanzaExcepcion() {
        assertThatThrownBy(() -> EstadoFisicoActivo.valueOf("INVALIDO"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
