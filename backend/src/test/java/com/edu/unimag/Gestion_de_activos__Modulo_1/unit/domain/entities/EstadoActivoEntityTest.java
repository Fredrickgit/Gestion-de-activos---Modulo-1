package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoActivoEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        EstadoActivo estadoActivo = EstadoActivo.builder()
                .id(ID)
                .estado(EstadoFisicoActivo.NUEVO)
                .descripcion("Recurso nuevo")
                .build();

        assertThat(estadoActivo.getId()).isEqualTo(ID);
        assertThat(estadoActivo.getEstado()).isEqualTo(EstadoFisicoActivo.NUEVO);
        assertThat(estadoActivo.getDescripcion()).isEqualTo("Recurso nuevo");
    }

    @Test
    @DisplayName("El constructor sin argumentos inicializa valores por defecto")
    void constructorSinArgumentos_inicializaValoresPorDefecto() {
        EstadoActivo estadoActivo = new EstadoActivo();

        assertThat(estadoActivo.getId()).isNull();
        assertThat(estadoActivo.getEstado()).isNull();
        assertThat(estadoActivo.getDescripcion()).isNull();
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        EstadoActivo estadoActivo = new EstadoActivo(ID, EstadoFisicoActivo.NUEVO, "Recurso nuevo");

        assertThat(estadoActivo.getId()).isEqualTo(ID);
        assertThat(estadoActivo.getEstado()).isEqualTo(EstadoFisicoActivo.NUEVO);
        assertThat(estadoActivo.getDescripcion()).isEqualTo("Recurso nuevo");
    }

    @Test
    @DisplayName("Los setters actualizan todos los campos")
    void setters_actualizanTodosLosCampos() {
        EstadoActivo estadoActivo = new EstadoActivo();

        estadoActivo.setId(ID);
        estadoActivo.setEstado(EstadoFisicoActivo.NUEVO);
        estadoActivo.setDescripcion("Recurso nuevo");

        assertThat(estadoActivo.getId()).isEqualTo(ID);
        assertThat(estadoActivo.getEstado()).isEqualTo(EstadoFisicoActivo.NUEVO);
        assertThat(estadoActivo.getDescripcion()).isEqualTo("Recurso nuevo");
    }
}
