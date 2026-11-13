package com.edu.unimag.Gestion_de_activos__Modulo_1.mock.applications;

import com.edu.unimag.Gestion_de_activos__Modulo_1.application.usecases.GenerarReporte;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GenerarReporteTest {

    @Mock
    private ActivoRepository activoRepository;

    @Mock
    private EspacioRepository espacioRepository;

    @InjectMocks
    private GenerarReporte generarReporte;

    @Test
    @DisplayName("Generar previsualización de reporte con ordenamiento predeterminado")
    void previsualizarReporteActivosExitoso() {
        Activo activo = Activo.builder()
                .id(UUID.randomUUID())
                .nombre("Laptop Dell")
                .codigo("ACT-500")
                .categoria(CategoriaRecurso.ACTIVO)
                .estadoFisicoId(UUID.randomUUID())
                .disponibilidadId(UUID.randomUUID())
                .espacioId(UUID.randomUUID())
                .tipoActivoId(UUID.randomUUID())
                .build();

        when(activoRepository.findAll()).thenReturn(List.of(activo));

        var resultado = generarReporte.previsualizarReporteActivos(0, null);

        assertNotNull(resultado);
        verify(activoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Lanzar excepción al navegar a una página sin registros")
    void previsualizarReportePaginaFueraDeRango() {
        when(activoRepository.findAll()).thenReturn(Collections.emptyList());

        DomainException ex = assertThrows(DomainException.class,
                () -> generarReporte.previsualizarReporteActivos(3, "nombre"));

        assertEquals("No hay más registros", ex.getMessage());
    }

    @Test
    @DisplayName("Lanzar excepción cuando el reporte filtrado por estado no arroje datos")
    void previsualizarReportePorEstadoSinResultados() {
        UUID estadoId = UUID.randomUUID();

        when(activoRepository.findByDisponibilidadId(estadoId)).thenReturn(Collections.emptyList());

        DomainException ex = assertThrows(DomainException.class,
                () -> generarReporte.previsualizarReporteActivosPorEstado(estadoId, 0));

        assertEquals("No hay recursos que coincidan con los filtros", ex.getMessage());
    }
}