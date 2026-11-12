package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.valueobjects;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCoordinatesException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Coordinates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinatesTest {

    @Test
    @DisplayName("Crea coordenadas validas")
    void of_creaCoordenadasValidas() {
        Coordinates coordinates = Coordinates.of(4.5709, -74.2973);

        assertThat(coordinates.latitude()).isEqualTo(4.5709);
        assertThat(coordinates.longitude()).isEqualTo(-74.2973);
    }

    @Test
    @DisplayName("Rechaza una latitud menor que -90")
    void of_rechazaLatitudMenorAlMinimo() {
        assertThatThrownBy(() -> Coordinates.of(-91, 0))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessage("La latitud debe estar entre -90 y 90");
    }

    @Test
    @DisplayName("Rechaza una latitud mayor que 90")
    void of_rechazaLatitudMayorAlMaximo() {
        assertThatThrownBy(() -> Coordinates.of(91, 0))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessage("La latitud debe estar entre -90 y 90");
    }

    @Test
    @DisplayName("Rechaza una longitud menor que -180")
    void of_rechazaLongitudMenorAlMinimo() {
        assertThatThrownBy(() -> Coordinates.of(0, -181))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessage("La longitud debe estar entre -180 y 180");
    }

    @Test
    @DisplayName("Rechaza una longitud mayor que 180")
    void of_rechazaLongitudMayorAlMaximo() {
        assertThatThrownBy(() -> Coordinates.of(0, 181))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessage("La longitud debe estar entre -180 y 180");
    }

    @Test
    @DisplayName("Rechaza una latitud no finita")
    void of_rechazaLatitudNoFinita() {
        assertThatThrownBy(() -> Coordinates.of(Double.NaN, 0))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessage("La latitud debe estar entre -90 y 90");
    }

    @Test
    @DisplayName("Rechaza una longitud no finita")
    void of_rechazaLongitudNoFinita() {
        assertThatThrownBy(() -> Coordinates.of(0, Double.POSITIVE_INFINITY))
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessage("La longitud debe estar entre -180 y 180");
    }

    @Test
    @DisplayName("Dos coordenadas con los mismos valores son iguales")
    void equals_comparaPorValor() {
        Coordinates coordinates = Coordinates.of(4.5709, -74.2973);
        Coordinates sameCoordinates = Coordinates.of(4.5709, -74.2973);

        assertThat(coordinates).isEqualTo(sameCoordinates);
        assertThat(coordinates).hasSameHashCodeAs(sameCoordinates);
    }

    @Test
    @DisplayName("Dos coordenadas con distinto valor no son iguales")
    void equals_rechazaValoresDistintos() {
        assertThat(Coordinates.of(4.5709, -74.2973))
                .isNotEqualTo(Coordinates.of(4.5709, -74.2974));
    }
}
