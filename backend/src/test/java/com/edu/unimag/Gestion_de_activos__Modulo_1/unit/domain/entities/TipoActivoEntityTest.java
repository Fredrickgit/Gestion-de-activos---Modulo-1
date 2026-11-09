package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.TipoActivo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidLoanPeriodException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        TipoActivo tipoActivo = new TipoActivo(ID, "Proyector", "Proyector de video", 5);

        assertThat(tipoActivo.getId()).isEqualTo(ID);
        assertThat(tipoActivo.getNombre()).isEqualTo("Proyector");
        assertThat(tipoActivo.getDescripcion()).isEqualTo("Proyector de video");
        assertThat(tipoActivo.getPlazoDevolucion()).isEqualTo(5);
    }

    @Test
    @DisplayName("El builder permite un plazo de devolucion cero")
    void builder_permitePlazoDevolucionCero() {
        TipoActivo tipoActivo = TipoActivo.builder()
                .id(ID)
                .nombre("Proyector")
                .descripcion("Proyector de video")
                .plazoDevolucion(0)
                .build();

        assertThat(tipoActivo.getPlazoDevolucion()).isZero();
    }

    @Test
    @DisplayName("El constructor permite una descripcion nula")
    void constructor_permiteDescripcionNula() {
        TipoActivo tipoActivo = new TipoActivo(ID, "Proyector", null, 5);

        assertThat(tipoActivo.getDescripcion()).isNull();
    }

    @Test
    @DisplayName("Rechaza el nombre nulo")
    void constructor_rechazaNombreNulo() {
        assertThatThrownBy(() -> new TipoActivo(ID, null, "Proyector de video", 5))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre del tipo de activo es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el nombre en blanco")
    void constructor_rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> new TipoActivo(ID, "   ", "Proyector de video", 5))
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre del tipo de activo es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el plazo de devolucion negativo")
    void constructor_rechazaPlazoDevolucionNegativo() {
        assertThatThrownBy(() -> new TipoActivo(ID, "Proyector", "Proyector de video", -1))
                .isInstanceOf(InvalidLoanPeriodException.class)
                .hasMessage("El plazo de devolucion no puede ser negativo");
    }
}
