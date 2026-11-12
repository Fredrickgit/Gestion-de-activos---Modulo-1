package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCapacityException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Capacity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CapacityTest {

    @Test
    @DisplayName("Crea una capacidad positiva")
    void of_creaCapacidadPositiva() {
        Capacity capacity = Capacity.of(30);

        assertThat(capacity.value()).isEqualTo(30);
    }

    @Test
    @DisplayName("Rechaza la capacidad en cero")
    void of_rechazaCapacidadCero() {
        assertThatThrownBy(() -> Capacity.of(0))
                .isInstanceOf(InvalidCapacityException.class)
                .hasMessage("El aforo del espacio debe ser mayor que cero");
    }

    @Test
    @DisplayName("Rechaza la capacidad negativa")
    void of_rechazaCapacidadNegativa() {
        assertThatThrownBy(() -> Capacity.of(-1))
                .isInstanceOf(InvalidCapacityException.class)
                .hasMessage("El aforo del espacio debe ser mayor que cero");
    }

    @Test
    @DisplayName("Dos capacidades con el mismo valor son iguales")
    void equals_comparaPorValor() {
        Capacity capacity = Capacity.of(30);
        Capacity sameCapacity = Capacity.of(30);

        assertThat(capacity).isEqualTo(sameCapacity);
        assertThat(capacity).hasSameHashCodeAs(sameCapacity);
    }

    @Test
    @DisplayName("Dos capacidades con distinto valor no son iguales")
    void equals_rechazaValoresDistintos() {
        assertThat(Capacity.of(30)).isNotEqualTo(Capacity.of(50));
    }
}
