package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.domain.entities;

import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Espacio;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.CodeRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCapacityException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidCategoryException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.InvalidLoanPeriodException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.NameRequiredException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.RequiredReferenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EspacioEntityTest {

    private static final UUID ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID FACULTAD_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID DISPONIBILIDAD_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID UBICACION_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    private static Espacio.EspacioBuilder espacioValido() {
        return Espacio.builder()
                .id(ID)
                .nombre("Laboratorio 101")
                .codigo("ESP-001")
                .aforo(30)
                .categoria(CategoriaRecurso.ESPACIO)
                .plazoDevolucion(2)
                .facultadId(FACULTAD_ID)
                .disponibilidadId(DISPONIBILIDAD_ID)
                .ubicacionId(UBICACION_ID);
    }

    @Test
    @DisplayName("El builder asigna todos los campos")
    void builder_asignaTodosLosCampos() {
        Espacio espacio = espacioValido().build();

        assertThat(espacio.getId()).isEqualTo(ID);
        assertThat(espacio.getNombre()).isEqualTo("Laboratorio 101");
        assertThat(espacio.getCodigo()).isEqualTo("ESP-001");
        assertThat(espacio.getAforo()).isEqualTo(30);
        assertThat(espacio.getCategoria()).isEqualTo(CategoriaRecurso.ESPACIO);
        assertThat(espacio.getPlazoDevolucion()).isEqualTo(2);
        assertThat(espacio.getFacultadId()).isEqualTo(FACULTAD_ID);
        assertThat(espacio.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(espacio.getUbicacionId()).isEqualTo(UBICACION_ID);
    }

    @Test
    @DisplayName("El constructor con todos los argumentos asigna todos los campos")
    void constructorConTodosLosArgumentos_asignaTodosLosCampos() {
        Espacio espacio = new Espacio(ID, "Laboratorio 101", "ESP-001", 30, CategoriaRecurso.ESPACIO, 2,
                FACULTAD_ID, DISPONIBILIDAD_ID, UBICACION_ID);

        assertThat(espacio.getId()).isEqualTo(ID);
        assertThat(espacio.getNombre()).isEqualTo("Laboratorio 101");
        assertThat(espacio.getCodigo()).isEqualTo("ESP-001");
        assertThat(espacio.getAforo()).isEqualTo(30);
        assertThat(espacio.getCategoria()).isEqualTo(CategoriaRecurso.ESPACIO);
        assertThat(espacio.getPlazoDevolucion()).isEqualTo(2);
        assertThat(espacio.getFacultadId()).isEqualTo(FACULTAD_ID);
        assertThat(espacio.getDisponibilidadId()).isEqualTo(DISPONIBILIDAD_ID);
        assertThat(espacio.getUbicacionId()).isEqualTo(UBICACION_ID);
    }

    @Test
    @DisplayName("El builder permite un id nulo")
    void builder_permiteIdNulo() {
        Espacio espacio = espacioValido().id(null).build();

        assertThat(espacio.getId()).isNull();
    }

    @Test
    @DisplayName("El builder permite una facultad nula")
    void builder_permiteFacultadNula() {
        Espacio espacio = espacioValido().facultadId(null).build();

        assertThat(espacio.getFacultadId()).isNull();
    }

    @Test
    @DisplayName("El builder permite un plazo de devolucion cero")
    void builder_permitePlazoDevolucionCero() {
        Espacio espacio = espacioValido().plazoDevolucion(0).build();

        assertThat(espacio.getPlazoDevolucion()).isZero();
    }

    @Test
    @DisplayName("Rechaza el nombre nulo")
    void constructor_rechazaNombreNulo() {
        assertThatThrownBy(() -> espacioValido().nombre(null).build())
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre del espacio es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el nombre en blanco")
    void constructor_rechazaNombreEnBlanco() {
        assertThatThrownBy(() -> espacioValido().nombre("   ").build())
                .isInstanceOf(NameRequiredException.class)
                .hasMessage("El nombre del espacio es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el codigo nulo")
    void constructor_rechazaCodigoNulo() {
        assertThatThrownBy(() -> espacioValido().codigo(null).build())
                .isInstanceOf(CodeRequiredException.class)
                .hasMessage("El codigo del espacio es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el codigo en blanco")
    void constructor_rechazaCodigoEnBlanco() {
        assertThatThrownBy(() -> espacioValido().codigo("   ").build())
                .isInstanceOf(CodeRequiredException.class)
                .hasMessage("El codigo del espacio es obligatorio");
    }

    @Test
    @DisplayName("Rechaza el aforo en cero")
    void constructor_rechazaAforoCero() {
        assertThatThrownBy(() -> espacioValido().aforo(0).build())
                .isInstanceOf(InvalidCapacityException.class)
                .hasMessage("El aforo del espacio debe ser mayor que cero");
    }

    @Test
    @DisplayName("Rechaza el aforo negativo")
    void constructor_rechazaAforoNegativo() {
        assertThatThrownBy(() -> espacioValido().aforo(-1).build())
                .isInstanceOf(InvalidCapacityException.class)
                .hasMessage("El aforo del espacio debe ser mayor que cero");
    }

    @Test
    @DisplayName("Rechaza una categoria distinta de ESPACIO")
    void constructor_rechazaCategoriaDistintaDeEspacio() {
        assertThatThrownBy(() -> espacioValido().categoria(CategoriaRecurso.ACTIVO).build())
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessage("La categoria del espacio debe ser ESPACIO");
    }

    @Test
    @DisplayName("Rechaza la categoria nula")
    void constructor_rechazaCategoriaNula() {
        assertThatThrownBy(() -> espacioValido().categoria(null).build())
                .isInstanceOf(InvalidCategoryException.class)
                .hasMessage("La categoria del espacio debe ser ESPACIO");
    }

    @Test
    @DisplayName("Rechaza el plazo de devolucion negativo")
    void constructor_rechazaPlazoDevolucionNegativo() {
        assertThatThrownBy(() -> espacioValido().plazoDevolucion(-1).build())
                .isInstanceOf(InvalidLoanPeriodException.class)
                .hasMessage("El plazo de devolucion no puede ser negativo");
    }

    @Test
    @DisplayName("Rechaza la disponibilidad nula")
    void constructor_rechazaDisponibilidadNula() {
        assertThatThrownBy(() -> espacioValido().disponibilidadId(null).build())
                .isInstanceOf(RequiredReferenceException.class)
                .hasMessage("La disponibilidad del espacio es obligatoria");
    }

    @Test
    @DisplayName("Rechaza la ubicacion nula")
    void constructor_rechazaUbicacionNula() {
        assertThatThrownBy(() -> espacioValido().ubicacionId(null).build())
                .isInstanceOf(RequiredReferenceException.class)
                .hasMessage("La ubicacion del espacio es obligatoria");
    }
}
