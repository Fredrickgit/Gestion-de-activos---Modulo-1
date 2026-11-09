package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ActivoEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ESTADO_FISICO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID DISPONIBILIDAD_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ESPACIO_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        Activo activo = Activo.builder()
                .id(ID)
                .nombre("Video Beam")
                .codigo("ACT-001")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(ESTADO_FISICO_ID)
                .disponibilidadId(DISPONIBILIDAD_ID)
                .espacioId(ESPACIO_ID)
                .build();

        assertThat(activo.getId()).isEqualTo(ID);
        assertThat(activo.getNombre()).isEqualTo("Video Beam");
        assertThat(activo.getCodigo()).isEqualTo("ACT-001");
        assertThat(activo.getCategoria()).isEqualTo(CategoriaRecurso.ACTIVO);
        assertThat(activo.getEstadoFisicoId()).isEqualTo(ESTADO_FISICO_ID);
        assertThat(activo.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(activo.getEspacioId()).isEqualTo(ESPACIO_ID);
    }

    @Test
    @DisplayName("El constructor sin argumentos inicializa valores por defecto")
    void constructorSinArgumentos_inicializaValoresPorDefecto() {
        Activo activo = new Activo();

        assertThat(activo.getId()).isNull();
        assertThat(activo.getNombre()).isNull();
        assertThat(activo.getCodigo()).isNull();
        assertThat(activo.getCategoria()).isNull();
        assertThat(activo.getEstadoFisicoId()).isNull();
        assertThat(activo.getDisponibilidadId()).isNull();
        assertThat(activo.getEspacioId()).isNull();
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        Activo activo = new Activo(ID, "Video Beam", "ACT-001", CategoriaRecurso.ACTIVO,
                ESTADO_FISICO_ID, DISPONIBILIDAD_ID, ESPACIO_ID);

        assertThat(activo.getId()).isEqualTo(ID);
        assertThat(activo.getNombre()).isEqualTo("Video Beam");
        assertThat(activo.getCodigo()).isEqualTo("ACT-001");
        assertThat(activo.getCategoria()).isEqualTo(CategoriaRecurso.ACTIVO);
        assertThat(activo.getEstadoFisicoId()).isEqualTo(ESTADO_FISICO_ID);
        assertThat(activo.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(activo.getEspacioId()).isEqualTo(ESPACIO_ID);
    }

    @Test
    @DisplayName("Los setters actualizan todos los campos")
    void setters_actualizanTodosLosCampos() {
        Activo activo = new Activo();

        activo.setId(ID);
        activo.setNombre("Video Beam");
        activo.setCodigo("ACT-001");
        activo.setCategoria(CategoriaRecurso.ACTIVO);
        activo.setEstadoFisicoId(ESTADO_FISICO_ID);
        activo.setDisponibilidadId(DISPONIBILIDAD_ID);
        activo.setEspacioId(ESPACIO_ID);

        assertThat(activo.getId()).isEqualTo(ID);
        assertThat(activo.getNombre()).isEqualTo("Video Beam");
        assertThat(activo.getCodigo()).isEqualTo("ACT-001");
        assertThat(activo.getCategoria()).isEqualTo(CategoriaRecurso.ACTIVO);
        assertThat(activo.getEstadoFisicoId()).isEqualTo(ESTADO_FISICO_ID);
        assertThat(activo.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(activo.getEspacioId()).isEqualTo(ESPACIO_ID);
    }
}
