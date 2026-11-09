package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.CodeRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCategoryException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.RequiredReferenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivoEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ESTADO_FISICO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID DISPONIBILIDAD_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID ESPACIO_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID TIPO_ACTIVO_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    private static Activo.ActivoBuilder activoValido() {
        return Activo.builder()
                .id(ID)
                .nombre("Video Beam")
                .codigo("ACT-001")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(ESTADO_FISICO_ID)
                .disponibilidadId(DISPONIBILIDAD_ID)
                .espacioId(ESPACIO_ID)
                .tipoActivoId(TIPO_ACTIVO_ID);
    }

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        Activo activo = activoValido().build();

        assertThat(activo.getId()).isEqualTo(ID);
        assertThat(activo.getNombre()).isEqualTo("Video Beam");
        assertThat(activo.getCodigo()).isEqualTo("ACT-001");
        assertThat(activo.getCategoria()).isEqualTo(CategoriaRecurso.ACTIVO);
        assertThat(activo.getEstadoFisicoId()).isEqualTo(ESTADO_FISICO_ID);
        assertThat(activo.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(activo.getEspacioId()).isEqualTo(ESPACIO_ID);
        assertThat(activo.getTipoActivoId()).isEqualTo(TIPO_ACTIVO_ID);
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        Activo activo = new Activo(ID, "Video Beam", "ACT-001", CategoriaRecurso.ACTIVO,
                ESTADO_FISICO_ID, DISPONIBILIDAD_ID, ESPACIO_ID, TIPO_ACTIVO_ID);

        assertThat(activo.getId()).isEqualTo(ID);
        assertThat(activo.getNombre()).isEqualTo("Video Beam");
        assertThat(activo.getCodigo()).isEqualTo("ACT-001");
        assertThat(activo.getCategoria()).isEqualTo(CategoriaRecurso.ACTIVO);
        assertThat(activo.getEstadoFisicoId()).isEqualTo(ESTADO_FISICO_ID);
        assertThat(activo.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(activo.getEspacioId()).isEqualTo(ESPACIO_ID);
        assertThat(activo.getTipoActivoId()).isEqualTo(TIPO_ACTIVO_ID);
    }

    @Test
    @DisplayName("El builder permite un id nulo")
    void builder_permiteIdNulo() {
        Activo activo = activoValido().id(null).build();

        assertThat(activo.getId()).isNull();
    }

    @Test
    @DisplayName("El builder permite un espacio nulo")
    void builder_permiteEspacioNulo() {
        Activo activo = activoValido().espacioId(null).build();

        assertThat(activo.getEspacioId()).isNull();
    }

    @Test
    @DisplayName("Rechaza el nombre nulo")
    void constructor_rechazaNombreNulo() {
        assertThatThrownBy(() -> activoValido().nombre(null).build())
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre del activo es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el nombre en blanco")
    void constructor_rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> activoValido().nombre("   ").build())
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre del activo es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el codigo nulo")
    void constructor_rechazaCodigoNulo() {
        assertThatThrownBy(() -> activoValido().codigo(null).build())
                .isInstanceOf(CodeRequiredException.class)
                .hasMessage("El codigo del activo es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el codigo en blanco")
    void constructor_rechazaCodigoEnBlanco() {
        assertThatThrownBy(() -> activoValido().codigo(" ").build())
                .isInstanceOf(CodeRequiredException.class)
                .hasMessage("El codigo del activo es obligatorio");
    }

    @Test
    @DisplayName("Rechaza una categoria distinta de ACTIVO")
    void constructor_rechazaCategoriaDistintaDeActivo() {
        assertThatThrownBy(() -> activoValido().categoria(CategoriaRecurso.ESPACIO).build())
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessage("La categoria del activo debe ser ACTIVO");
    }

    @Test
    @DisplayName("Rechaza la categoria nula")
    void constructor_rechazaCategoriaNula() {
        assertThatThrownBy(() -> activoValido().categoria(null).build())
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessage("La categoria del activo debe ser ACTIVO");
    }

    @Test
    @DisplayName("Rechaza el estado fisico nulo")
    void constructor_rechazaEstadoFisicoNulo() {
        assertThatThrownBy(() -> activoValido().estadoFisicoId(null).build())
                .isInstanceOf(RequiredReferenceException.class)
                .hasMessage("El estado fisico del activo es obligatorio");
    }

    @Test
    @DisplayName("Rechaza la disponibilidad nula")
    void constructor_rechazaDisponibilidadNula() {
        assertThatThrownBy(() -> activoValido().disponibilidadId(null).build())
                .isInstanceOf(RequiredReferenceException.class)
                .hasMessage("La disponibilidad del activo es obligatoria");
    }

    @Test
    @DisplayName("Rechaza el tipo de activo nulo")
    void constructor_rechazaTipoActivoNulo() {
        assertThatThrownBy(() -> activoValido().tipoActivoId(null).build())
                .isInstanceOf(RequiredReferenceException.class)
                .hasMessage("El tipo de activo es obligatorio");
    }
}
