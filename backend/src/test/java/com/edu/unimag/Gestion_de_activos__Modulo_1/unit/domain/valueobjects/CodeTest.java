package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.CodeRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Code;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CodeTest {

    private static final String REQUIRED_MESSAGE = "El codigo es obligatorio";

    @Test
    @DisplayName("Crea un codigo valido")
    void of_creaCodigoValido() {
        Code code = Code.of("ACT-001", REQUIRED_MESSAGE);

        assertThat(code.value()).isEqualTo("ACT-001");
        assertThat(code.toString()).isEqualTo("ACT-001");
    }

    @Test
    @DisplayName("Rechaza el codigo nulo")
    void of_rechazaCodigoNulo() {
        assertThatThrownBy(() -> Code.of(null, REQUIRED_MESSAGE))
                .isInstanceOf(CodeRequiredException.class)
                .hasMessage(REQUIRED_MESSAGE);
    }

    @Test
    @DisplayName("Rechaza el codigo en blanco")
    void of_rechazaCodigoEnBlanco() {
        assertThatThrownBy(() -> Code.of("   ", REQUIRED_MESSAGE))
                .isInstanceOf(CodeRequiredException.class)
                .hasMessage(REQUIRED_MESSAGE);
    }

    @Test
    @DisplayName("Dos codigos con el mismo valor son iguales")
    void equals_comparaPorValor() {
        Code code = Code.of("ACT-001", REQUIRED_MESSAGE);
        Code sameCode = Code.of("ACT-001", REQUIRED_MESSAGE);

        assertThat(code).isEqualTo(sameCode);
        assertThat(code).hasSameHashCodeAs(sameCode);
    }

    @Test
    @DisplayName("Dos codigos con distinto valor no son iguales")
    void equals_rechazaValoresDistintos() {
        assertThat(Code.of("ACT-001", REQUIRED_MESSAGE))
                .isNotEqualTo(Code.of("ACT-002", REQUIRED_MESSAGE));
    }
}
