package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Facultad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("El constructor sin argumentos inicializa valores por defecto")
    void constructorSinArgumentos_inicializaValoresPorDefecto() {
        Facultad facultad = new Facultad();

        assertThat(facultad.getId()).isNull();
        assertThat(facultad.getNombre()).isNull();
        assertThat(facultad.getDescripcion()).isNull();
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
    @DisplayName("Los setters actualizan todos los campos")
    void setters_actualizanTodosLosCampos() {
        Facultad facultad = new Facultad();

        facultad.setId(ID);
        facultad.setNombre("Facultad de Ingeniería");
        facultad.setDescripcion("Sede principal");

        assertThat(facultad.getId()).isEqualTo(ID);
        assertThat(facultad.getNombre()).isEqualTo("Facultad de Ingeniería");
        assertThat(facultad.getDescripcion()).isEqualTo("Sede principal");
    }
}
