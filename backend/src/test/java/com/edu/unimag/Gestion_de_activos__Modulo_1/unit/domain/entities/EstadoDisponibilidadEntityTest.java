package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoDisponibilidadRecurso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoDisponibilidadEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        EstadoDisponibilidad estadoDisponibilidad = EstadoDisponibilidad.builder()
                .id(ID)
                .disponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE)
                .descripcion("Disponible para préstamo")
                .build();

        assertThat(estadoDisponibilidad.getId()).isEqualTo(ID);
        assertThat(estadoDisponibilidad.getDisponibilidadRecurso()).isEqualTo(EstadoDisponibilidadRecurso.DISPONIBLE);
        assertThat(estadoDisponibilidad.getDescripcion()).isEqualTo("Disponible para préstamo");
    }

    @Test
    @DisplayName("El constructor sin argumentos inicializa valores por defecto")
    void constructorSinArgumentos_inicializaValoresPorDefecto() {
        EstadoDisponibilidad estadoDisponibilidad = new EstadoDisponibilidad();

        assertThat(estadoDisponibilidad.getId()).isNull();
        assertThat(estadoDisponibilidad.getDisponibilidadRecurso()).isNull();
        assertThat(estadoDisponibilidad.getDescripcion()).isNull();
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        EstadoDisponibilidad estadoDisponibilidad = new EstadoDisponibilidad(
                ID, EstadoDisponibilidadRecurso.DISPONIBLE, "Disponible para préstamo");

        assertThat(estadoDisponibilidad.getId()).isEqualTo(ID);
        assertThat(estadoDisponibilidad.getDisponibilidadRecurso()).isEqualTo(EstadoDisponibilidadRecurso.DISPONIBLE);
        assertThat(estadoDisponibilidad.getDescripcion()).isEqualTo("Disponible para préstamo");
    }

    @Test
    @DisplayName("Los setters actualizan todos los campos")
    void setters_actualizanTodosLosCampos() {
        EstadoDisponibilidad estadoDisponibilidad = new EstadoDisponibilidad();

        estadoDisponibilidad.setId(ID);
        estadoDisponibilidad.setDisponibilidadRecurso(EstadoDisponibilidadRecurso.DISPONIBLE);
        estadoDisponibilidad.setDescripcion("Disponible para préstamo");

        assertThat(estadoDisponibilidad.getId()).isEqualTo(ID);
        assertThat(estadoDisponibilidad.getDisponibilidadRecurso()).isEqualTo(EstadoDisponibilidadRecurso.DISPONIBLE);
        assertThat(estadoDisponibilidad.getDescripcion()).isEqualTo("Disponible para préstamo");
    }
}
