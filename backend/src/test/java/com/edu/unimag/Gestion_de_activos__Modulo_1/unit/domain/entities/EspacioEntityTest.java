package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EspacioEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FACULTAD_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID DISPONIBILIDAD_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID UBICACION_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        Espacio espacio = Espacio.builder()
                .id(ID)
                .nombre("Laboratorio 101")
                .aforo(30)
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(2)
                .facultadId(FACULTAD_ID)
                .disponibilidadId(DISPONIBILIDAD_ID)
                .ubicacionId(UBICACION_ID)
                .build();

        assertThat(espacio.getId()).isEqualTo(ID);
        assertThat(espacio.getNombre()).isEqualTo("Laboratorio 101");
        assertThat(espacio.getAforo()).isEqualTo(30);
        assertThat(espacio.getCategoria()).isEqualTo(CategoriaRecurso.ESPACIO);
        assertThat(espacio.getPlazoDevolucion()).isEqualTo(2);
        assertThat(espacio.getFacultadId()).isEqualTo(FACULTAD_ID);
        assertThat(espacio.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(espacio.getUbicacionId()).isEqualTo(UBICACION_ID);
    }

    @Test
    @DisplayName("El constructor sin argumentos inicializa valores por defecto")
    void constructorSinArgumentos_inicializaValoresPorDefecto() {
        Espacio espacio = new Espacio();

        assertThat(espacio.getId()).isNull();
        assertThat(espacio.getNombre()).isNull();
        assertThat(espacio.getAforo()).isZero();
        assertThat(espacio.getCategoria()).isNull();
        assertThat(espacio.getPlazoDevolucion()).isZero();
        assertThat(espacio.getFacultadId()).isNull();
        assertThat(espacio.getDisponibilidadId()).isNull();
        assertThat(espacio.getUbicacionId()).isNull();
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        Espacio espacio = new Espacio(ID, "Laboratorio 101", 30, CategoriaRecurso.ESPACIO, 2,
                FACULTAD_ID, DISPONIBILIDAD_ID, UBICACION_ID);

        assertThat(espacio.getId()).isEqualTo(ID);
        assertThat(espacio.getNombre()).isEqualTo("Laboratorio 101");
        assertThat(espacio.getAforo()).isEqualTo(30);
        assertThat(espacio.getCategoria()).isEqualTo(CategoriaRecurso.ESPACIO);
        assertThat(espacio.getPlazoDevolucion()).isEqualTo(2);
        assertThat(espacio.getFacultadId()).isEqualTo(FACULTAD_ID);
        assertThat(espacio.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(espacio.getUbicacionId()).isEqualTo(UBICACION_ID);
    }

    @Test
    @DisplayName("Los setters actualizan todos los campos")
    void setters_actualizanTodosLosCampos() {
        Espacio espacio = new Espacio();

        espacio.setId(ID);
        espacio.setNombre("Laboratorio 101");
        espacio.setAforo(30);
        espacio.setCategoria(CategoriaRecurso.ESPACIO);
        espacio.setPlazoDevolucion(2);
        espacio.setFacultadId(FACULTAD_ID);
        espacio.setDisponibilidadId(DISPONIBILIDAD_ID);
        espacio.setUbicacionId(UBICACION_ID);

        assertThat(espacio.getId()).isEqualTo(ID);
        assertThat(espacio.getNombre()).isEqualTo("Laboratorio 101");
        assertThat(espacio.getAforo()).isEqualTo(30);
        assertThat(espacio.getCategoria()).isEqualTo(CategoriaRecurso.ESPACIO);
        assertThat(espacio.getPlazoDevolucion()).isEqualTo(2);
        assertThat(espacio.getFacultadId()).isEqualTo(FACULTAD_ID);
        assertThat(espacio.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(espacio.getUbicacionId()).isEqualTo(UBICACION_ID);
    }
}
