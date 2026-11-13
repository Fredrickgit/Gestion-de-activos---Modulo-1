package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameTest {

    private static final String REQUIRED_MESSAGE = "El nombre es obligatorio";

    @Test
    @DisplayName("Crea un nombre valido")
    void of_creaNombreValido() {
        Name name = Name.of("Video Beam", REQUIRED_MESSAGE);

        assertThat(name.value()).isEqualTo("Video Beam");
        assertThat(name.toString()).isEqualTo("Video Beam");
    }

    @Test
    @DisplayName("Rechaza el nombre nulo")
    void of_rechazaNombreNulo() {
        assertThatThrownBy(() -> Name.of(null, REQUIRED_MESSAGE))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage(REQUIRED_MESSAGE);
    }

    @Test
    @DisplayName("Rechaza el nombre en blanco")
    void of_rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> Name.of("   ", REQUIRED_MESSAGE))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage(REQUIRED_MESSAGE);
    }

    @Test
    @DisplayName("Dos nombres con el mismo valor son iguales")
    void equals_comparaPorValor() {
        Name name = Name.of("Video Beam", REQUIRED_MESSAGE);
        Name sameName = Name.of("Video Beam", REQUIRED_MESSAGE);

        assertThat(name).isEqualTo(sameName);
        assertThat(name).hasSameHashCodeAs(sameName);
    }

    @Test
    @DisplayName("Dos nombres con distinto valor no son iguales")
    void equals_rechazaValoresDistintos() {
        assertThat(Name.of("Video Beam", REQUIRED_MESSAGE))
                .isNotEqualTo(Name.of("Proyector", REQUIRED_MESSAGE));
    }
}
