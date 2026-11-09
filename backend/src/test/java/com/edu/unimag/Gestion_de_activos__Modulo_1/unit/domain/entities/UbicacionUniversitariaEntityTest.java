package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCoordinatesException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.valueobjects.Coordinates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UbicacionUniversitariaEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Coordinates COORDENADAS = Coordinates.of(4.5709, -74.2973);

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        UbicacionUniversitaria ubicacion = UbicacionUniversitaria.builder()
                .id(ID)
                .nombre("Bloque A")
                .descripcion("Edificio de aulas")
                .coordenadas(COORDENADAS)
                .build();

        assertThat(ubicacion.getId()).isEqualTo(ID);
        assertThat(ubicacion.getNombre()).isEqualTo("Bloque A");
        assertThat(ubicacion.getDescripcion()).isEqualTo("Edificio de aulas");
        assertThat(ubicacion.getCoordenadas()).isEqualTo(COORDENADAS);
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        UbicacionUniversitaria ubicacion = new UbicacionUniversitaria(
                ID, "Bloque A", "Edificio de aulas", COORDENADAS);

        assertThat(ubicacion.getId()).isEqualTo(ID);
        assertThat(ubicacion.getNombre()).isEqualTo("Bloque A");
        assertThat(ubicacion.getDescripcion()).isEqualTo("Edificio de aulas");
        assertThat(ubicacion.getCoordenadas()).isEqualTo(COORDENADAS);
    }

    @Test
    @DisplayName("El constructor sin coordenadas deja el campo nulo")
    void constructorSinCoordenadas_dejaCoordenadasNulas() {
        UbicacionUniversitaria ubicacion = new UbicacionUniversitaria(ID, "Bloque A", "Edificio de aulas");

        assertThat(ubicacion.getCoordenadas()).isNull();
    }

    @Test
    @DisplayName("El builder permite coordenadas nulas")
    void builder_permiteCoordenadasNulas() {
        UbicacionUniversitaria ubicacion = UbicacionUniversitaria.builder()
                .id(ID)
                .nombre("Bloque A")
                .descripcion("Edificio de aulas")
                .build();

        assertThat(ubicacion.getCoordenadas()).isNull();
    }

    @Test
    @DisplayName("Rechaza coordenadas fuera de rango")
    void constructor_rechazaCoordenadasInvalidas() {
        assertThatThrownBy(() -> UbicacionUniversitaria.builder()
                .id(ID)
                .nombre("Bloque A")
                .coordenadas(Coordinates.of(91, -74.2973))
                .build())
                .isInstanceOf(InvalidCoordinatesException.class)
                .hasMessage("La latitud debe estar entre -90 y 90");
    }

    @Test
    @DisplayName("Rechaza el nombre nulo")
    void constructor_rechazaNombreNulo() {
        assertThatThrownBy(() -> new UbicacionUniversitaria(ID, null, "Edificio de aulas"))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre de la ubicacion es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el nombre en blanco")
    void constructor_rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> new UbicacionUniversitaria(ID, "   ", "Edificio de aulas"))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre de la ubicacion es obligatorio");
    }
}
