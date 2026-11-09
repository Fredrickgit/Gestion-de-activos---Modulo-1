package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TipoActivoEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        TipoActivo tipoActivo = TipoActivo.builder()
                .id(ID)
                .nombre("Proyector")
                .descripcion("Proyector de video")
                .plazoDevolucion(5)
                .build();

        assertThat(tipoActivo.getId()).isEqualTo(ID);
        assertThat(tipoActivo.getNombre()).isEqualTo("Proyector");
        assertThat(tipoActivo.getDescripcion()).isEqualTo("Proyector de video");
        assertThat(tipoActivo.getPlazoDevolucion()).isEqualTo(5);
    }

    @Test
    @DisplayName("El constructor sin argumentos inicializa valores por defecto")
    void constructorSinArgumentos_inicializaValoresPorDefecto() {
        TipoActivo tipoActivo = new TipoActivo();

        assertThat(tipoActivo.getId()).isNull();
        assertThat(tipoActivo.getNombre()).isNull();
        assertThat(tipoActivo.getDescripcion()).isNull();
        assertThat(tipoActivo.getPlazoDevolucion()).isZero();
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        TipoActivo tipoActivo = new TipoActivo(ID, "Proyector", "Proyector de video", 5);

        assertThat(tipoActivo.getId()).isEqualTo(ID);
        assertThat(tipoActivo.getNombre()).isEqualTo("Proyector");
        assertThat(tipoActivo.getDescripcion()).isEqualTo("Proyector de video");
        assertThat(tipoActivo.getPlazoDevolucion()).isEqualTo(5);
    }

    @Test
    @DisplayName("Los setters actualizan todos los campos")
    void setters_actualizanTodosLosCampos() {
        TipoActivo tipoActivo = new TipoActivo();

        tipoActivo.setId(ID);
        tipoActivo.setNombre("Proyector");
        tipoActivo.setDescripcion("Proyector de video");
        tipoActivo.setPlazoDevolucion(5);

        assertThat(tipoActivo.getId()).isEqualTo(ID);
        assertThat(tipoActivo.getNombre()).isEqualTo("Proyector");
        assertThat(tipoActivo.getDescripcion()).isEqualTo("Proyector de video");
        assertThat(tipoActivo.getPlazoDevolucion()).isEqualTo(5);
    }
}
