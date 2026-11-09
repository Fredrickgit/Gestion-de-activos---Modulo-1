package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.UbicacionUniversitaria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UbicacionUniversitariaEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        UbicacionUniversitaria ubicacion = UbicacionUniversitaria.builder()
                .id(ID)
                .nombre("Bloque A")
                .descripcion("Edificio de aulas")
                .build();

        assertThat(ubicacion.getId()).isEqualTo(ID);
        assertThat(ubicacion.getNombre()).isEqualTo("Bloque A");
        assertThat(ubicacion.getDescripcion()).isEqualTo("Edificio de aulas");
    }

    @Test
    @DisplayName("El constructor sin argumentos inicializa valores por defecto")
    void constructorSinArgumentos_inicializaValoresPorDefecto() {
        UbicacionUniversitaria ubicacion = new UbicacionUniversitaria();

        assertThat(ubicacion.getId()).isNull();
        assertThat(ubicacion.getNombre()).isNull();
        assertThat(ubicacion.getDescripcion()).isNull();
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        UbicacionUniversitaria ubicacion = new UbicacionUniversitaria(ID, "Bloque A", "Edificio de aulas");

        assertThat(ubicacion.getId()).isEqualTo(ID);
        assertThat(ubicacion.getNombre()).isEqualTo("Bloque A");
        assertThat(ubicacion.getDescripcion()).isEqualTo("Edificio de aulas");
    }

    @Test
    @DisplayName("Los setters actualizan todos los campos")
    void setters_actualizanTodosLosCampos() {
        UbicacionUniversitaria ubicacion = new UbicacionUniversitaria();

        ubicacion.setId(ID);
        ubicacion.setNombre("Bloque A");
        ubicacion.setDescripcion("Edificio de aulas");

        assertThat(ubicacion.getId()).isEqualTo(ID);
        assertThat(ubicacion.getNombre()).isEqualTo("Bloque A");
        assertThat(ubicacion.getDescripcion()).isEqualTo("Edificio de aulas");
    }
}
