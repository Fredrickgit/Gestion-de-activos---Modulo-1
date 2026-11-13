package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.EstadoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.EstadoFisicoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.PhysicalStateRequiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        EstadoActivo estadoActivo = new EstadoActivo(ID, EstadoFisicoActivo.NUEVO, "Recurso nuevo");

        assertThat(estadoActivo.getId()).isEqualTo(ID);
        assertThat(estadoActivo.getEstado()).isEqualTo(EstadoFisicoActivo.NUEVO);
        assertThat(estadoActivo.getDescripcion()).isEqualTo("Recurso nuevo");
    }

    @Test
    @DisplayName("El constructor permite una descripcion nula")
    void constructor_permiteDescripcionNula() {
        EstadoActivo estadoActivo = new EstadoActivo(ID, EstadoFisicoActivo.NUEVO, null);

        assertThat(estadoActivo.getDescripcion()).isNull();
    }

    @Test
    @DisplayName("Rechaza el estado nulo")
    void constructor_rechazaEstadoNulo() {
        assertThatThrownBy(() -> new EstadoActivo(ID, null, "Recurso nuevo"))
                .isInstanceOf(PhysicalStateRequiredException.class)
                .hasMessage("El estado fisico del activo es obligatorio");
    }
}
