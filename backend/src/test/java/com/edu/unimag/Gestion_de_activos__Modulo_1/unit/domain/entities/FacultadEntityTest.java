package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FacultadEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        Facultad facultad = Facultad.builder()
                .id(ID)
                .nombre("Facultad de Ingeniería")
                .descripcion("Sede principal")
                .build();

        assertThat(facultad.getId()).isEqualTo(ID);
        assertThat(facultad.getNombre()).isEqualTo("Facultad de Ingeniería");
        assertThat(facultad.getDescripcion()).isEqualTo("Sede principal");
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        Facultad facultad = new Facultad(ID, "Facultad de Ingeniería", "Sede principal");

        assertThat(facultad.getId()).isEqualTo(ID);
        assertThat(facultad.getNombre()).isEqualTo("Facultad de Ingeniería");
        assertThat(facultad.getDescripcion()).isEqualTo("Sede principal");
    }

    @Test
    @DisplayName("El constructor permite una descripcion nula")
    void constructor_permiteDescripcionNula() {
        Facultad facultad = new Facultad(ID, "Facultad de Ingeniería", null);

        assertThat(facultad.getDescripcion()).isNull();
    }

    @Test
    @DisplayName("Rechaza el nombre nulo")
    void constructor_rechazaNombreNulo() {
        assertThatThrownBy(() -> new Facultad(ID, null, "Sede principal"))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre de la facultad es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el nombre en blanco")
    void constructor_rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> new Facultad(ID, "   ", "Sede principal"))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre de la facultad es obligatorio");
    }
}
