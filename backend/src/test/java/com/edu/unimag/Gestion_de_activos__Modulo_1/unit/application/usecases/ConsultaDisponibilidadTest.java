package com.edu.unimag.Gestion_de_activos__Modulo_1.unit.application.usecases;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.ConsultaDisponibilidad;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.Activo;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.entities.enums.CategoriaRecurso;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.exceptions.DomainException;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.ActivoRepository;
import com.edu.unimag.Gestion_de_activos__Modulo_1.domain.ports.EspacioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ConsultaDisponibilidadTest {

    @Mock
    private ActivoRepository activoRepository;

    @Mock
    private EspacioRepository espacioRepository;

    @InjectMocks
    private ConsultaDisponibilidad consultaDisponibilidad;

    @Test
    @DisplayName("Retornar ID de disponibilidad cuando el activo existe")
    void consultarDisponibilidadActivoExitoso() {
        UUID activoId = UUID.randomUUID();
        UUID disponibilidadId = UUID.randomUUID();
        Activo activo = Activo.builder()
                .id(activoId)
                .nombre("Microscopio")
                .codigo("ACT-100")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(UUID.randomUUID())
                .disponibilidadId(disponibilidadId)
                .espacioId(UUID.randomUUID())
                .tipoActivoId(UUID.randomUUID())
                .build();

        when(activoRepository.findById(activoId)).thenReturn(Optional.of(activo));

        UUID resultado = consultaDisponibilidad.consultarDisponibilidadActivo(activoId);

        assertEquals(disponibilidadId, resultado);
        verify(activoRepository, times(1)).findById(activoId);
    }

    @Test
    @DisplayName("Lanzar excepción cuando el recurso no existe")
    void consultarDisponibilidadActivoNoEncontrado() {
        UUID activoId = UUID.randomUUID();

        when(activoRepository.findById(activoId)).thenReturn(Optional.empty());

        DomainException ex = assertThrows(DomainException.class,
                () -> consultaDisponibilidad.consultarDisponibilidadActivo(activoId));

        assertEquals("Recurso no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Consultar disponibilidad paginada por categoría")
    void consultarDisponibilidadActivosPorCategoriaExitoso() {
        Activo a1 = Activo.builder()
                .id(UUID.randomUUID())
                .nombre("Osciloscopio")
                .codigo("ACT-101")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(UUID.randomUUID())
                .disponibilidadId(UUID.randomUUID())
                .espacioId(UUID.randomUUID())
                .tipoActivoId(UUID.randomUUID())
                .build();

        when(activoRepository.findByCategoria(CategoriaRecurso.ACTIVO)).thenReturn(List.of(a1));

        consultaDisponibilidad.consultarDisponibilidadActivosPorCategoria(CategoriaRecurso.ACTIVO, 0);

        verify(activoRepository, times(1)).findByCategoria(CategoriaRecurso.ACTIVO);
    }

    @Test
    @DisplayName("Lanzar excepción si no hay recursos en la categoría")
    void consultarDisponibilidadCategoriaSinResultados() {
        when(activoRepository.findByCategoria(CategoriaRecurso.ACTIVO)).thenReturn(Collections.emptyList());

        DomainException ex = assertThrows(DomainException.class,
                () -> consultaDisponibilidad.consultarDisponibilidadActivosPorCategoria(CategoriaRecurso.ACTIVO, 0));

        assertEquals("No se encontraron activos para la categoría especificada", ex.getMessage());
    }
}